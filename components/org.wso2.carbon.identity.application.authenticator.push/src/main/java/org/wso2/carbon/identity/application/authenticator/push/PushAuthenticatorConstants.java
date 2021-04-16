/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.identity.application.authenticator.push;

/**
 * This is a utils class for Push Authenticator Constants.
 */
public class PushAuthenticatorConstants {

    public static final String AUTHENTICATOR_NAME = "push";
    public static final String AUTHENTICATOR_FRIENDLY_NAME = "Push Authenticator";
    public static final String AUTHENTICATION_STATUS = "Authentication Failed !";
    public static final String SERVER_KEY = "ServerKey";
    public static final String FCM_URL = "fcmUrl";
    public static final String SIGNED_CHALLENGE = "signedChallenge";
    public static final String PUSH_AUTH_CHALLENGE = "pushAuthChallenge";
    public static final String AUTHORIZATION = "Authorization";
    public static final String POST = "POST";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String BODY = "body";
    public static final String CONTENT_AVAILABLE = "content_available";
    public static final String HIGH = "high";
    public static final String PRIORITY = "priority";
    public static final String DEVICE_ID = "deviceId";
    public static final String CHALLENGE = "challenge";
    public static final String USERNAME = "username";
    public static final String FULL_NAME = "displayName";
    public static final String ORGANIZATION_NAME = "organization";
    public static final String APPLICATION_NAME = "applicationName";
    public static final String APPLICATION_URL = "applicationUrl";
    public static final String IP_ADDRESS = "ipAddress";
    public static final String REQUEST_DEVICE_OS = "deviceName";
    public static final String REQUEST_DEVICE_BROWSER = "browserName";
    public static final String CLICK_ACTION = "click_action";
    public static final String DISPLAY_ANDROID_ACTIVITY = "AuthenticateActivity";
    public static final String TO = "to";
    public static final String NOTIFICATION = "notification";
    public static final String DATA = "data";
    public static final String WAIT_PAGE = "authenticationendpoint/wait.jsp";
    public static final String DEVICES_PAGE = "authenticationendpoint/push-devices.jsp";
    public static final String PUSH_ENDPOINT = "/push-auth/check-status";
    public static final String POLLING_QUERY_PARAMS = "?sessionDataKey=";
    public static final String COMPLETED = "COMPLETED";
    public static final String PUSH_AUTH_WAIT = "wait";
    public static final String PUSH_AUTH_ACCESS_DENIED = "access-denied";
    public static final String AUTH_REQUEST_STATUS_SUCCESS = "SUCCESSFUL";
    public static final String AUTH_REQUEST_STATUS_DENIED = "DENIED";

    public static final String FIRST_NAME_CLAIM = "http://wso2.org/claims/givenname";
    public static final String LAST_NAME_CLAIM = "http://wso2.org/claims/lastname";

    public static final String PUSH_AUTHENTICATION_ENDPOINT_WAIT_URL = "pushAuthenticationEndpointWaitURL";
    public static final String PUSH_AUTHENTICATION_ENDPOINT_DEVICES_URL = "pushAuthenticationEndpointDevicesURL";
}
