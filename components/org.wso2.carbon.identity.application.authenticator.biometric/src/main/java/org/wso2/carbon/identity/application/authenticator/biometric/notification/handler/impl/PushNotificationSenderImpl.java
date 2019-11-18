/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.carbon.identity.application.authenticator.biometric.notification.handler.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.wso2.carbon.identity.application.authenticator.biometric.notification.handler.PushNotificationSender;
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
    public static PushNotificationSenderImpl getInstance() {
        return pushNotificationinstance;
    }

    /**
     * Method to send push notification to Android FireBased Cloud messaging
     * Server.
     *
     * @param deviceId        Generated and provided from Android Client Developer
     * @param serverKey       Key which is Generated in FCM Server
     * @param message         which contains actual information
     * @param randomChallenge which contains the random challenge
     * @param sessionDataKey  which contains the session data key of each push notification.
     */
    @Override
    public void sendPushNotification(String deviceId, String serverKey,
                                     String message, String randomChallenge, String sessionDataKey) {

        try {
            String fcmUrl = "https://fcm.googleapis.com/fcm/send";
            URL url = new URL(fcmUrl);

            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + serverKey);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject infoJson = new JSONObject();
            infoJson.put("body", message);
            infoJson.put("content_available", true);
            infoJson.put("priority", "high");

            JSONObject dataJson = new JSONObject();
            dataJson.put("body", message);
            dataJson.put("Challenge", randomChallenge);
            dataJson.put("sessionkey", sessionDataKey);
            dataJson.put("click_action", "AuthenticateActivity");
            dataJson.put("content_available", true);
            dataJson.put("priority", "high");

            JSONObject json = new JSONObject();
            json.put("to", deviceId.trim());
            json.put("notification", infoJson);
            json.put("data", dataJson);
            json.put("content_available", true);
            json.put("priority", "high");

            log.info("info json is: "+ infoJson.toString());
            log.info("data json is: "+ dataJson.toString());

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            int status = 0;
            status = conn.getResponseCode();
            if (status != 0) {
                if (status == 200) {
                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(conn.getInputStream()));
                    log.debug("Android Notification Response : " + reader.readLine());
                    log.debug("returned UUID : " + randomChallenge);
                } else if (status == 401) {
//client side error
                    log.debug("Notification Response : TokenId : " + deviceId + " Error occurred :");
                } else if (status == 501) {
//server side error
                    log.debug("Notification Response : [ errorCode=ServerError ] DeviceId : " + deviceId);
                } else if (status == 503) {
//server side error
                    log.debug("Notification Response : FCM Service is Unavailable DeviceId : " + deviceId);
                }
            }
        } catch (MalformedURLException malfexception) {
// Protocol Error
            log.debug("Error occurred while sending push Notification!.." + malfexception.getMessage());
        } catch (Exception malfexception) {
//URL problem
            log.debug("Reading URL, Error occurred while sending push Notification!.." + malfexception.getMessage());
        }
    }
}
