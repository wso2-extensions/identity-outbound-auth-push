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
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authenticator.biometric.BiometricAuthenticator;
import org.wso2.carbon.identity.application.authenticator.biometric.BiometricAuthenticatorConstants;
import org.wso2.carbon.identity.application.authenticator.biometric.notification.handler.PushNotificationSender;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Implements the sending of push notifications to mobile device IDs.
 */
public class FirebasePushNotificationSenderImpl implements PushNotificationSender {

    private static final Log log = LogFactory.getLog(FirebasePushNotificationSenderImpl.class);
    private static FirebasePushNotificationSenderImpl pushNotificationinstance = new FirebasePushNotificationSenderImpl();

    public static FirebasePushNotificationSenderImpl getInstance() {
        return pushNotificationinstance;
    }

    /**
     * Method to send push notification to Android FireBase Cloud messaging
     * Server.
     *
     * @param deviceId        Generated and provided from Android Client Developer
     * @param serverKey       Key which is Generated in FCM Server
     * @param message         which contains actual information
     * @param randomChallenge which contains a random challenge for each push notification
     * @param sessionDataKey  which contains the session data key for each push notification.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void sendPushNotification(String deviceId, String serverKey,
                                     String message, String randomChallenge, String sessionDataKey) throws AuthenticationFailedException {
        // TODO: 2019-11-20 make serverkey an initialization param.

        try {
//            BiometricAuthenticator biometricAuthenticator = new BiometricAuthenticator();
//            biometricAuthenticator.
            String fcmUrl = "https://fcm.googleapis.com/fcm/send";

            //String fcmUrl = authenticatorProperties.get(BiometricAuthenticatorConstants.FCM_URL);
            // TODO: 2019-11-20 add as a config property in biometric authenticator properties.
            URL url = new URL(fcmUrl);
            // TODO: 2019-11-20 use okhttp/apacheHTTPclient instead of httpurlconnection
            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + serverKey);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject biometricNotificationInfo = new JSONObject();
            biometricNotificationInfo.put("body", message);
            biometricNotificationInfo.put("content_available", true);
            biometricNotificationInfo.put("priority", "high");

            JSONObject biometricNotificationData = new JSONObject();
            biometricNotificationData.put("body", message);
            biometricNotificationData.put("Challenge", randomChallenge);
            biometricNotificationData.put("sessionkey", sessionDataKey);
            //Reason for sending the click_action in the data payload is to specifically open a different activity in android app except the default main activity.
            biometricNotificationData.put("click_action", "AuthenticateActivity");
            biometricNotificationData.put("content_available", true);
            biometricNotificationData.put("priority", "high");
            // TODO: 2019-11-20 define contants for all keys

            JSONObject json = new JSONObject();
            json.put("to", deviceId.trim());
            json.put("notification", biometricNotificationInfo);
            json.put("data", biometricNotificationData);
            json.put("content_available", true);
            json.put("priority", "high");

            if (log.isDebugEnabled()) {
                log.debug("Firebase message payload: " + json.toString());
            }

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            int status = conn.getResponseCode();
            // TODO: 2019-11-20 refer firebase docs and handle all errors .(if status!=200)
            if (status == 401) {
                log.error("Notification Response : Device Id : " + deviceId + " Error occurred ");

            } else if (status == 501) {
                log.debug("Notification Response : [ errorCode=ServerError ] DeviceId : " + deviceId);

            } else if (status == 503) {
                log.debug("Notification Response : FCM Service is Unavailable DeviceId : " + deviceId);
            }

        } catch (MalformedURLException e) {
            throw new AuthenticationFailedException("Invalid URL ", e);
        }catch (ProtocolException e) {
            throw new AuthenticationFailedException("Error while setting the HTTP method ", e);
        }catch (IOException e) {
            throw new AuthenticationFailedException("Authentication failed!. An IOException was caught. ", e);
        }
    }

}

