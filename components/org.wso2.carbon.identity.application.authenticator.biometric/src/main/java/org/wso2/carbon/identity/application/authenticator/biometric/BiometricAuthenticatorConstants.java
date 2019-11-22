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
    public static final String SESSION_DATA_KEY = "sessionDataKey";
    // TODO: 2019-11-20 use CONTEXT_KEY from auth framework
    public static final String SERVER_KEY = "ServerKey";
    public static final String FCM_URL = "fcmUrl";
    public static final String SIGNED_CHALLENGE = "signedChallenge";
    public static final String BIOMETRIC_AUTH_CHALLENGE = "biometricAuthChallenge";
}
