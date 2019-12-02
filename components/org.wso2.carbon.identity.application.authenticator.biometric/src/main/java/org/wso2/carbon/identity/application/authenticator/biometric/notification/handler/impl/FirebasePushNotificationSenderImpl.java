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
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authenticator.biometric.BiometricAuthenticatorConstants;
import org.wso2.carbon.identity.application.authenticator.biometric.notification.handler.PushNotificationSender;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletResponse;


/**
 * Implements the sending of push notifications to mobile device IDs.
 */
public class FirebasePushNotificationSenderImpl implements PushNotificationSender {

    private static final Log log = LogFactory.getLog(FirebasePushNotificationSenderImpl.class);
    private static FirebasePushNotificationSenderImpl pushNotificationinstance;
    private String serverKey;
    private String fcmUrl;

    public void init(String serverKey, String fcmUrl) {

        this.serverKey = serverKey;
        this.fcmUrl = fcmUrl;
    }

    public static synchronized FirebasePushNotificationSenderImpl getInstance() {

        if (pushNotificationinstance == null) {
            pushNotificationinstance = new FirebasePushNotificationSenderImpl();
        }
        return pushNotificationinstance;
    }

    /**
     * Method to send push notification to Android FireBase Cloud messaging
     * Server.
     *
     * @param deviceId        Generated and provided from Android Client Developer
     * @param message         which contains actual information
     * @param randomChallenge which contains a random challenge for each push notification
     * @param sessionDataKey  which contains the session data key for each push notification.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void sendPushNotification(String deviceId,
                                     String message, String randomChallenge, String sessionDataKey)
            throws AuthenticationFailedException {

        try {
            URL url = new URL(fcmUrl);
            HttpURLConnection conn;

            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod(BiometricAuthenticatorConstants.POST);
            conn.setRequestProperty(BiometricAuthenticatorConstants.AUTHORIZATION, "key=" + serverKey);
            conn.setRequestProperty(BiometricAuthenticatorConstants.CONTENT_TYPE,
                    BiometricAuthenticatorConstants.APPLICATION_JSON);

            JSONObject biometricNotificationInfo = new JSONObject();
            biometricNotificationInfo.put(BiometricAuthenticatorConstants.BODY, message);
            biometricNotificationInfo.put(BiometricAuthenticatorConstants.CONTENT_AVAILABLE, true);
            biometricNotificationInfo.put(BiometricAuthenticatorConstants.PRIORITY,
                    BiometricAuthenticatorConstants.HIGH);

            JSONObject biometricNotificationData = new JSONObject();
            biometricNotificationData.put(BiometricAuthenticatorConstants.BODY, message);
            biometricNotificationData.put(BiometricAuthenticatorConstants.CHALLENGE, randomChallenge);
            biometricNotificationData.put(BiometricAuthenticatorConstants.CONTEXT_KEY, sessionDataKey);
            //Reason for sending the click_action in the data payload is to
            // specifically open a different activity in android app except the default main activity.
            biometricNotificationData.put(BiometricAuthenticatorConstants.CLICK_ACTION,
                    BiometricAuthenticatorConstants.DISPLAY_ANDROID_ACTIVITY);
            biometricNotificationData.put(BiometricAuthenticatorConstants.CONTENT_AVAILABLE, true);
            biometricNotificationData.put(BiometricAuthenticatorConstants.PRIORITY,
                    BiometricAuthenticatorConstants.HIGH);

            JSONObject json = new JSONObject();
            json.put(BiometricAuthenticatorConstants.TO, deviceId.trim());
            json.put(BiometricAuthenticatorConstants.NOTIFICATION, biometricNotificationInfo);
            json.put(BiometricAuthenticatorConstants.DATA, biometricNotificationData);
            json.put(BiometricAuthenticatorConstants.CONTENT_AVAILABLE, true);
            json.put(BiometricAuthenticatorConstants.PRIORITY, BiometricAuthenticatorConstants.HIGH);

            if (log.isDebugEnabled()) {
                log.debug("Firebase message payload: " + json.toString());
            }

            OutputStreamWriter wr = null;
            try {
                wr = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
                wr.write(json.toString());
                wr.flush();
            } finally {
                if (wr != null) {
                    wr.close();
                }
            }

            int status = conn.getResponseCode();
            if (status != HttpServletResponse.SC_OK) {
                if (status == HttpServletResponse.SC_BAD_REQUEST) {
                    log.error("Request parameters were invalid.");

                } else if (status == HttpServletResponse.SC_UNAUTHORIZED) {
                    log.error("Notification Response : Device Id : " + deviceId + " Error occurred.");

                } else if (status == HttpServletResponse.SC_NOT_FOUND) {
                    log.error("App instance was unregistered from FCM.Token used is no longer valid. ");

                } else if (status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                    log.error(" An unknown internal error occurred.");

                } else if (status == HttpServletResponse.SC_SERVICE_UNAVAILABLE) {
                    log.error("The server is overloaded. DeviceId : " + deviceId);
                } else {
                    log.error("Some unknown error occurred when initiating the push notification. ");
                }
            }


        } catch (MalformedURLException e) {
            throw new AuthenticationFailedException("Invalid URL ", e);
        } catch (ProtocolException e) {
            throw new AuthenticationFailedException("Error while setting the HTTP method ", e);
        } catch (IOException e) {
            throw new AuthenticationFailedException("Authentication failed!. An IOException was caught. ", e);
        }
    }
}
