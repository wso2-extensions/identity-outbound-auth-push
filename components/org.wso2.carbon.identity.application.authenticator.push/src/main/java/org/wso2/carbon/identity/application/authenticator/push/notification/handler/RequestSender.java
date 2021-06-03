package org.wso2.carbon.identity.application.authenticator.push.notification.handler;

import org.wso2.carbon.identity.application.authenticator.push.exception.PushAuthenticatorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Processes auth request to be sent as push notifications.
 */
public interface RequestSender {

    /**
     * Send the authentication request to the mobile app using FCM.
     *
     * @param request  HTTP Request
     * @param response HTTP Response
     * @param deviceId Device ID of the authenticating device
     * @param key      Session Data Key
     * @throws PushAuthenticatorException if an error occurs while preparing the push notification
     */
    void sendRequest(HttpServletRequest request, HttpServletResponse response,
                     String deviceId, String key) throws PushAuthenticatorException;
}
