/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 */

package org.wso2.carbon.identity.application.authenticator.push.device.handler;

/**
 * This class contains the constants used in this module.
 */
public class DeviceHandlerConstants {

    public static final String USERNAME = "USER_NAME";
    public static final String DOMAIN_NAME = "DOMAIN_NAME";
    public static final String USER_STORE = "USER_STORE";
    public static final String USER_ID = "USER_ID";
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String MODEL = "MODEL";
    public static final String PUSH_ID = "PUSH_ID";
    public static final String PUBLIC_KEY = "PUBLIC_KEY";
    public static final String REGISTRATION_TIME = "REGISTRATION_TIME";
    public static final String LAST_USED_TIME = "LAST_USED_TIME";
    public static final String REGISTRATION_ENDPOINT = "/push-auth/devices";
    public static final String REMOVE_DEVICE_ENDPOINT = "/push-auth/devices/remove";
    public static final String AUTHENTICATION_ENDPOINT = "/push-auth/authenticate";
    public static final String GIVEN_NAME_USER_CLAIM = "http://wso2.org/claims/givenname";
    public static final String LAST_NAME_USER_CLAIM = "http://wso2.org/claims/lastname";

    public static final String HASHING_ALGORITHM = "SHA256withRSA";
    public static final String SIGNATURE_ALGORITHM = "RSA";

    /**
     * This class contains the SQL queries used in the DAO class.
     */
    public static class SQLQueries {

        private SQLQueries() {

        }

        public static final String REGISTER_DEVICE = "INSERT INTO PUSH_AUTHENTICATION_DEVICE " +
                "(ID,USER_ID,NAME,MODEL,PUSH_ID,PUBLIC_KEY,REGISTRATION_TIME,LAST_USED_TIME)" +
                " VALUES (?,?,?,?,?,?,?,?)";
        public static final String UNREGISTER_DEVICE = "DELETE FROM PUSH_AUTHENTICATION_DEVICE WHERE ID = ?";
        public static final String EDIT_DEVICE = "UPDATE PUSH_AUTHENTICATION_DEVICE SET NAME = ?, PUSH_ID = ?, " +
                "PUBLIC_KEY = ? WHERE ID = ?";
        public static final String GET_DEVICE = "SELECT ID,NAME,MODEL,PUSH_ID,PUBLIC_KEY," +
                "REGISTRATION_TIME,LAST_USED_TIME FROM PUSH_AUTHENTICATION_DEVICE WHERE ID = ?";
        public static final String LIST_DEVICES = "SELECT ID,NAME,MODEL,REGISTRATION_TIME," +
                "LAST_USED_TIME FROM PUSH_AUTHENTICATION_DEVICE WHERE USER_ID = ?";
        public static final String GET_PUBLIC_KEY = "SELECT PUBLIC_KEY FROM PUSH_AUTHENTICATION_DEVICE " +
                "WHERE ID = ?";
    }
}