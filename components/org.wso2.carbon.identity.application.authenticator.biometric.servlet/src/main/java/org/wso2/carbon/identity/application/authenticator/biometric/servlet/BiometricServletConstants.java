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

package org.wso2.carbon.identity.application.authenticator.biometric.servlet;

/**
 * class for Biometric Endpoint Constants.
 */
public class BiometricServletConstants {

    public static final String INITIATOR = "initiator";
    public static final String MOBILE = "mobile";
    public static final String WEB = "web";
    public static final String CONTEXT_KEY = "sessionDataKey";
    public static final String CHALLENGE = "challenge";
    public static final String APPLICATION_JSON = "application/json";
    public static final String SIGNED_CHALLENGE = "signedChallenge";
    public static final String BIOMETRIC_ENDPOINT = "/biometric-auth";

    /**
     * Object holding wait status. The status of the response from mobile is either COMPLETED or REJECTED.
     */
    public enum Status {

        COMPLETED, REJECTED
    }
}
