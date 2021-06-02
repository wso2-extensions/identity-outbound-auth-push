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

import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;

/**
 * Handles the sending of push notifications to specific device IDs.
 */
public interface PushNotificationSender {

    /**
     * Send a push notification to a specific device ID with the session data key, randomChallenge and the message.
     */
    void sendPushNotification(String deviceId, String pushId, String message,
                              String randomChallenge, String sessionDataKey, String username,
                              String fullName, String organization, String serviceProvideName,
                              String hostName, String userOS, String userBrowser)
            throws AuthenticationFailedException;
}
