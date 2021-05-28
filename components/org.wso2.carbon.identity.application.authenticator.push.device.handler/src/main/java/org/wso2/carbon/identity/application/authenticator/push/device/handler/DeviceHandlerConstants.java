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
    public static final String DEVICE_ID = "ID";
    public static final String DEVICE_NAME = "NAME";
    public static final String DEVICE_MODEL = "MODEL";
    public static final String PUSH_ID = "PUSH_ID";
    public static final String PUBLIC_KEY = "PUBLIC_KEY";
    public static final String REGISTRATION_TIME = "REGISTRATION_TIME";
    public static final String LAST_USED_TIME = "LAST_USED_TIME";
    public static final String REGISTRATION_ENDPOINT = "/push-auth/devices";
    public static final String REMOVE_DEVICE_ENDPOINT = "/push-auth/devices/remove";
    public static final String AUTHENTICATION_ENDPOINT = "/push-auth/authenticate";
    public static final String TENANT_QUALIFIED_PATH = "/t/";
    public static final String ME_API_PATH = "/api/users/v1/me";
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
        public static final String REMOVE_USER_DEVICES = "DELETE FROM PUSH_AUTHENTICATION_DEVICE WHERE USER_ID = ?";
        public static final String EDIT_DEVICE = "UPDATE PUSH_AUTHENTICATION_DEVICE SET NAME = ?, PUSH_ID = ? " +
                "WHERE ID = ?";
        public static final String GET_DEVICE = "SELECT ID,NAME,MODEL,PUSH_ID,PUBLIC_KEY," +
                "REGISTRATION_TIME,LAST_USED_TIME FROM PUSH_AUTHENTICATION_DEVICE WHERE ID = ?";
        public static final String LIST_DEVICES = "SELECT ID,NAME,MODEL,REGISTRATION_TIME," +
                "LAST_USED_TIME FROM PUSH_AUTHENTICATION_DEVICE WHERE USER_ID = ?";
        public static final String GET_PUBLIC_KEY = "SELECT PUBLIC_KEY FROM PUSH_AUTHENTICATION_DEVICE " +
                "WHERE ID = ?";
    }

    /**
     * Enum which contains error codes and corresponding error messages.
     */
    public enum ErrorMessages {

        ERROR_CODE_CACHE_ENTRY_NOT_FOUND(
                "PDM-10001",
                "Unidentified request when trying to register device: %s. "
                        + "Registration cache entry was not found."
        ),
        ERROR_CODE_DEVICE_ALREADY_REGISTERED(
                "PDM-10002",
                "The device: %s is already registered."
        ),
        ERROR_CODE_UNREGISTER_UNAVAILABLE_DEVICE(
                "PDM-10003",
                "Failed to remove device: %s as it was not found."
        ),
        ERROR_CODE_UNREGISTER_DEVICE_FAILED(
                "PDM-10004",
                "Error occurred when trying to unregister device: %s."
        ),
        ERROR_CODE_REMOVE_ALL_DEVICES_FAILED(
                "PDM-10005",
                "Error occurred when trying to remove all the registered devices for user: %s."
        ),
        ERROR_CODE_EDIT_DEVICE_NOT_FOUND(
                "PDM-10006",
                "Failed to edit device %s. Device may not be registered."
        ),
        ERROR_CODE_DEVICE_NOT_FOUND(
                "PDM-10007",
                "Device: %s was not found."
        ),
        ERROR_CODE_GET_DEVICE_FAILED(
                "PDM-10008",
                "Error occurred when trying to get device: %s."
        ),
        ERROR_CODE_DEVICE_LIST_NOT_FOUND(
                "PDM-10009",
                "Error occurred when trying to get the device list for user with ID: %s."
        ),
        ERROR_CODE_PUBLIC_KEY_NOT_FOUND(
                "PDM-10010",
                "Failed to get public key as device: %s was not found."
        ),
        ERROR_CODE_GET_PUBLIC_KEY_FAILED(
                "PDM-10011",
                "Error occurred when trying to get the pubic key for device: %s."
        ),
        ERROR_CODE_GET_USER_CLAIMS_FAILED(
                "PDM-10012",
                "Error occurred when trying to get the user claims to get discovery data for user: %s."
        ),
        ERROR_CODE_INVALID_SIGNATURE(
                "PDM-10013",
                "Could not verify signature to register device: %s."
        ),
        ERROR_CODE_SIGNATURE_VERIFICATION_FAILED(
                "PDM-10014",
                "Error occurred when trying to verify the signature for device: %s."
        ),
        ERROR_CODE_FAILED_TO_GET_USER_ID(
                "PDM-10015",
                "Error occurred when trying to get the user ID to register device: %s."
        ),
        ERROR_CODE_REGISTER_DEVICE_FAILED(
                "PDM-10016",
                "Error occurred when trying to register device: %s."
        ),
        ERROR_CODE_EDIT_DEVICE_FAILED(
                "PDM-10017",
                "Error occurred when trying to update the device: %s."
        );



        private final String code;
        private final String message;

        ErrorMessages(String code, String message) {

            this.code = code;
            this.message = message;
        }

        public String getCode() {

            return code;
        }

        public String getMessage() {

            return message;
        }

        @Override
        public String toString() {

            return code + " - " + message;
        }

    }
}
