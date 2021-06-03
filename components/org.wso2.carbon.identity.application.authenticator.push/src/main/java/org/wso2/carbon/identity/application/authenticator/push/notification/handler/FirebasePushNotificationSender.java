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

package org.wso2.carbon.identity.application.authenticator.push.notification.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.inbound.InboundConstants;
import org.wso2.carbon.identity.application.authenticator.push.PushAuthenticatorConstants;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

/**
 * This class implements the sending of push notifications via Firebase to mobile device IDs.
 */
public class FirebasePushNotificationSender {

    private static final Log log = LogFactory.getLog(FirebasePushNotificationSender.class);
    private static FirebasePushNotificationSender pushNotificationInstance;
    private String serverKey;
    private String fcmUrl;

    private FirebasePushNotificationSender() {

    }

    public void init(String serverKey, String fcmUrl) {

        this.serverKey = serverKey;
        this.fcmUrl = fcmUrl;
    }

    public static synchronized FirebasePushNotificationSender getInstance() {

        if (pushNotificationInstance == null) {
            pushNotificationInstance = new FirebasePushNotificationSender();
        }
        return pushNotificationInstance;
    }

    /**
     * Method to send push notification to mobile app via Firebase Cloud messaging.
     *
     * @param deviceId              Unique ID of the device the notification is sent to
     * @param pushId                Unique ID provided from mobile client
     * @param message               Message to be displayed in the push notification
     * @param randomChallenge       Random challenge for the authentication request
     * @param sessionDataKey        Session data key for the authentication request
     * @param username              Username of the user requesting to authenticate
     * @param fullName              Full name of the user requesting to authenticate
     * @param organization          Tenant domain of the user requesting to authenticate
     * @param serviceProviderName   Service provider the user is attempting to access
     * @param hostname              IP address of the user's device
     * @param userOS                Operating system of the user's device
     * @param userBrowser           Browser used by the user to authenticate
     * @throws AuthenticationFailedException if an error occurs while sending the push notification request
     */
    public void sendPushNotification(String deviceId, String pushId, String message, String randomChallenge,
                                     String sessionDataKey, String username, String fullName, String organization,
                                     String serviceProviderName, String hostname, String userOS, String userBrowser)
            throws AuthenticationFailedException {

        try {
            URL url = new URL(fcmUrl);
            HttpURLConnection conn;

            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod(PushAuthenticatorConstants.POST);
            conn.setRequestProperty(PushAuthenticatorConstants.AUTHORIZATION, "key=" + serverKey);
            conn.setRequestProperty(PushAuthenticatorConstants.CONTENT_TYPE, MediaType.APPLICATION_JSON);

            JSONObject pushNotificationInfo = new JSONObject();
            pushNotificationInfo.put(PushAuthenticatorConstants.BODY, message);

            JSONObject pushNotificationData = new JSONObject();
            pushNotificationData.put(PushAuthenticatorConstants.DEVICE_ID, deviceId);
            pushNotificationData.put(PushAuthenticatorConstants.BODY, message);
            pushNotificationData.put(PushAuthenticatorConstants.CHALLENGE, randomChallenge);
            pushNotificationData.put(InboundConstants.RequestProcessor.CONTEXT_KEY, sessionDataKey);
            pushNotificationData.put(PushAuthenticatorConstants.USERNAME, username);
            pushNotificationData.put(PushAuthenticatorConstants.FULL_NAME, fullName);
            pushNotificationData.put(PushAuthenticatorConstants.ORGANIZATION_NAME, organization);
            pushNotificationData.put(PushAuthenticatorConstants.APPLICATION_NAME, serviceProviderName);
            pushNotificationData.put(PushAuthenticatorConstants.IP_ADDRESS, hostname);
            pushNotificationData.put(PushAuthenticatorConstants.REQUEST_DEVICE_BROWSER, userBrowser);
            pushNotificationData.put(PushAuthenticatorConstants.REQUEST_DEVICE_OS, userOS);

            JSONObject json = new JSONObject();
            json.put(PushAuthenticatorConstants.TO, pushId.trim());
            json.put(PushAuthenticatorConstants.NOTIFICATION, pushNotificationInfo);
            json.put(PushAuthenticatorConstants.DATA, pushNotificationData);
            json.put(PushAuthenticatorConstants.CONTENT_AVAILABLE, true);
            json.put(PushAuthenticatorConstants.PRIORITY, PushAuthenticatorConstants.HIGH);

            if (log.isDebugEnabled()) {
                log.debug("Firebase message payload: " + json.toJSONString());
            }

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            wr.write(json.toString());
            wr.flush();
            wr.close();

            int status = conn.getResponseCode();
            if (status != HttpServletResponse.SC_OK) {
                if (status == HttpServletResponse.SC_BAD_REQUEST) {
                    log.error("Request parameters were invalid.");
                } else if (status == HttpServletResponse.SC_UNAUTHORIZED) {
                    log.error("Notification Response : Device Id : " + deviceId + " Error occurred.");
                } else if (status == HttpServletResponse.SC_NOT_FOUND) {
                    log.error("App instance was unregistered from FCM. Token used is no longer valid.");
                } else if (status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                    log.error("An unknown internal error occurred.");
                } else if (status == HttpServletResponse.SC_SERVICE_UNAVAILABLE) {
                    log.error("The server is overloaded. DeviceId : " + deviceId);
                } else {
                    log.error("Some unknown error occurred when initiating the push notification.");
                }
            }

        } catch (MalformedURLException e) {
            throw new AuthenticationFailedException("Invalid URL. The required firebase URL is " + fcmUrl, e);
        } catch (ProtocolException e) {
            throw new AuthenticationFailedException("Error while setting the HTTP method ", e);
        } catch (IOException e) {
            throw new AuthenticationFailedException("Authentication failed!. An IOException was caught. ", e);
        }
    }
}
