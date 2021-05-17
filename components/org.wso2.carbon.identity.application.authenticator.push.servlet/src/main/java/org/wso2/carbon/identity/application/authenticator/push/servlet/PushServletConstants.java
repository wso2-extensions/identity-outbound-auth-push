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

package org.wso2.carbon.identity.application.authenticator.push.servlet;

/**
 * This class contains constants used for push endpoint component.
 */
public class PushServletConstants {

    public static final String PUSH_AUTH_ENDPOINT = "/push-auth/authenticate";
    public static final String PUSH_AUTH_STATUS_ENDPOINT = "/push-auth/check-status";
    public static final String PUSH_AUTH_SEND_ENDPOINT = "/push-auth/send";
    public static final String SESSION_DATA_KEY = "sessionDataKey";
    public static final String DEVICE_ID = "deviceId";
    public static final String AUTH_DATA = "authData";
    public static final String AUTH_RESPONSE = "authResponse";

    /**
     * Object holding authentication mobile response status.
     */
    public enum Status {
        COMPLETED, PENDING
    }
}
