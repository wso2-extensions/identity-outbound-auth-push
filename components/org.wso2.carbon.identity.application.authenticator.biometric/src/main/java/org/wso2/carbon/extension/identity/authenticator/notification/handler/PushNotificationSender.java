package org.wso2.carbon.extension.identity.authenticator.notification.handler;

/**
 * Handles the sending of push notifications to specific device IDs.
 */
public interface PushNotificationSender {

    void sendPushNotification(String deviceId, String serverKey,
                              String message, String randomUUIDString, String sessionDataKey);
}
