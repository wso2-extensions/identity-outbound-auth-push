package org.wso2.carbon.extension.identity.authenticator.notification.handler.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.wso2.carbon.extension.identity.authenticator.notification.handler.PushNotificationSender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implements the sending of push notifications to specific device IDs.
 */
public class PushNotificationSenderImpl implements PushNotificationSender {

    private static final Log log = LogFactory.getLog(PushNotificationSenderImpl.class);
    private static PushNotificationSenderImpl pushNotificationinstance = new PushNotificationSenderImpl();

    /**
     * Method to send push notification to Android FireBased Cloud messaging
     * Server.
     *  @param deviceId    Generated and provided from Android Client Developer
     * @param serverKey Key which is Generated in FCM Server
     * @param message    which contains actual information
     * @param randomChallenge    which contains the random challenge
     * @param sessionDataKey    which contains the session data key of each push notification.
     */
    @Override
    public void sendPushNotification(String deviceId, String serverKey,
                                     String message, String randomChallenge, String sessionDataKey) {

        try {
            String fcmUrl = "https://fcm.googleapis.com/fcm/send";
// Create URL instance.

            URL url = new URL(fcmUrl);
// create connection.
            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
//set method as POST or GET
            conn.setRequestMethod("POST");
//pass FCM server key
            conn.setRequestProperty("Authorization", "key=" + serverKey);
//Specify Message Format
            conn.setRequestProperty("Content-Type", "application/json");
//Create JSON Object & pass value
            JSONObject infoJson = new JSONObject();

            infoJson.put("title", "Welcome to WSO2 verify!!");
            infoJson.put("body", message);
            //infoJson.put("enter your uuid", randomUUIDString);
            infoJson.put("content_available" , true);
            infoJson.put("priority", "high");

            JSONObject dataJson = new JSONObject();

            dataJson.put("title", "Welcome to WSO2 verify!!");
            dataJson.put("body", message);
            dataJson.put("Challenge", randomChallenge);
            dataJson.put("sessionkey", sessionDataKey);
            dataJson.put("click_action", "AuthenticateActivity");
            dataJson.put("content_available" , true);
            dataJson.put("priority", "high");


            JSONObject json = new JSONObject();
            json.put("to", deviceId.trim());
            json.put("notification", infoJson);
            json.put("data", dataJson);
            json.put("content_available" , true);
            json.put("priority", "high");


            log.info("json :" + json.toString());
            log.info("infoJson :" + infoJson.toString());
            log.info("dataJson :" + dataJson.toString());
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            int status = 0;
            if (null != conn) {
                status = conn.getResponseCode();
            }
            if (status != 0) {

                if (status == 200) {
//SUCCESS message
                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(conn.getInputStream()));

                    log.info("Android Notification Response : " + reader.readLine());
                    log.info("returned UUID : " + randomChallenge);
                } else if (status == 401) {
//client side error
                    log.info("Notification Response : TokenId : " + deviceId + " Error occurred :");
                } else if (status == 501) {
//server side error
                    log.info("Notification Response : [ errorCode=ServerError ] TokenId : " + deviceId);
                } else if (status == 503) {
//server side error
                    log.info("Notification Response : FCM Service is Unavailable TokenId : " + deviceId);
                }
            }
        } catch (MalformedURLException mlfexception) {
// Prototcal Error
            log.info("Error occurred while sending push Notification!.." + mlfexception.getMessage());
        } catch (Exception mlfexception) {
//URL problem
            log.info("Reading URL, Error occurred while sending push Notification!.." + mlfexception.getMessage());
        }


    }

    public static PushNotificationSenderImpl getInstance() {
        return pushNotificationinstance;
    }
}
