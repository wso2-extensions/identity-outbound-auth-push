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

package org.wso2.carbon.identity.application.authenticator.biometric;

/**
 * Utils class for Biometric Authenticator Constants.
 */
public class BiometricAuthenticatorConstants {

    public static final String AUTHENTICATOR_NAME = "biometric";
    public static final String AUTHENTICATOR_FRIENDLY_NAME = "Biometric Authenticator";
    public static final String AUTHENTICATION_STATUS = "Authentication Failed !";
    public static final String CONTEXT_KEY = "sessionDataKey";
    public static final String SERVER_KEY = "ServerKey";
    public static final String FCM_URL = "fcmUrl";
    public static final String SIGNED_CHALLENGE = "signedChallenge";
    public static final String BIOMETRIC_AUTH_CHALLENGE = "biometricAuthChallenge";
    public static final String AUTHORIZATION = "Authorization";
    public static final String POST = "POST";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String BODY = "body";
    public static final String CONTENT_AVAILABLE = "content_available";
    public static final String HIGH = "high";
    public static final String PRIORITY = "priority";
    public static final String CHALLENGE = "Challenge";
    public static final String SESSION_KEY = "sessionkey";
    public static final String CLICK_ACTION = "click_action";
    public static final String DISPLAY_ANDROID_ACTIVITY = "AuthenticateActivity";
    public static final String TO = "to";
    public static final String NOTIFICATION = "notification";
    public static final String DATA = "data";
    public static final String DOMAIN_NAME = "https://biometricauthenticator.private.wso2.com:9443/";
    public static final String WAIT_PAGE = "authenticationendpoint/wait.jsp";
    public static final String BIOMETRIC_ENDPOINT = "/biometric-auth";
    public static final String POLLING_QUERY_PARAMS = "?initiator=web&sessionDataKey=";
    public static final String COMPLETED = "COMPLETED";
}
