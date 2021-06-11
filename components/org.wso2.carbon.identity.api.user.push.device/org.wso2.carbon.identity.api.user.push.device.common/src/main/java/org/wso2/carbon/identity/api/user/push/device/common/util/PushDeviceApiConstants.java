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
 *
 */

package org.wso2.carbon.identity.api.user.push.device.common.util;

/**
 * Constants used in Push Device handler Rest APIs.
 */
public class PushDeviceApiConstants {

    public static final String RESULT_SUCCESSFUL = "SUCCESSFUL";
    public static final String RESULT_FAILED = "FAILED";
    public static final String OPERATION_REGISTER = "REGISTER";
    public static final String OPERATION_REMOVE = "REMOVE";

    /**
     * Error definitions.
     */
    public enum ErrorMessages {
        ERROR_CODE_INVALID_SIGNATURE(
                "PDM-15001",
                "The credibility of the request could not be verified",
                "An error occurred when the digital signature is not verified"),

        ERROR_CODE_DEVICE_HANDLER_SQL_EXCEPTION(
                "PDM-15002",
                "The operation was not executed due to a database error",
                "A SQL exception occurred while trying to perform database operation "),
        ERROR_CODE__JSON_PROCESSING_EXCEPTION(
                "PDM-15003",
                "Error occurred while trying to convert an object to json format",
                "" + "A System error occurred while trying to convert an object to a JSON"),
        ERROR_CODE_INTERNAL_SERVER_ERROR(
                "PDM-15004",
                "Failed to process request due to an Internal server error",
                "A System error occurred internally while trying to process a request"),
        ERROR_CODE_USER_STORE_ERROR(
                "PDM-15005",
                "Failed to get User store manager of the user",
                "A System error occurred internally while trying to get a UserStoreManager"),
        ERROR_CODE_FAILED_SIGNATURE_VALIDATION(
                "PDM-15006",
                "Failed to get User store manager of the user",
                "A System error occurred internally while trying to get a UserStoreManager"),
        ERROR_CODE_GET_DEVICE_SERVER_ERROR(
                "PDM-15007",
                "A system error occurred while trying to retrieve a device"),
        ERROR_CODE_LIST_DEVICE_SERVER_ERROR(
                "PDM-15008",
                "A system error occurred while trying to retrieve registered devices of a user"),
        ERROR_CODE_UNREGISTER_DEVICE_SERVER_ERROR(
                "PDM-15009",
                "A system error occurred while trying to remove a specific device"),
        ERROR_CODE_EDIT_DEVICE_SERVER_ERROR(
                "PDM-15010",
                "The device could not be modified",
                "A System error occurred while trying to modify the device information"),
        ERROR_CODE_REGISTER_DEVICE_SERVER_ERROR(
                "PDM-15011",
                "A system error occurred while trying to register a new device"),
        ERROR_CODE_REGISTRATION_DISCOVERY_DATA_ERROR(
                "PDM-15012",
                "A system error occurred while trying to get registration discovery data"),
        ERROR_CODE_IO_ERROR(
                "PDM-15013",
                "A system error thrown while converting a JSON to an object"),
        ERROR_CODE_REGISTER_DEVICE_CLIENT_ERROR(
                "PDM-10001",
                "A system error occurred while trying to register a new device"),
        ERROR_CODE_GET_DEVICE_CLIENT_ERROR(
                "PDM-10002",
                "A system error occurred while trying to retrieve a device"),
        ERROR_CODE_LIST_DEVICE_CLIENT_ERROR(
                "PDM-10003",
                "A system error occurred while trying to retrieve registered devices of a user"),
        ERROR_CODE_UNREGISTER_DEVICE_CLIENT_ERROR(
                "PDM-10004",
                "A System error occurred while trying to remove a specific device"),
        ERROR_CODE_EDIT_DEVICE_CLIENT_ERROR(
                "PDM-10005",
                "The device could not be modified",
                "A System error occurred while trying to modify the device information");

        private final String code;
        private String message;
        private final String description;

        ErrorMessages(String code, String message, String description) {

            this.code = code;
            this.message = message;
            this.description = description;
        }

        ErrorMessages(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public void setMessage(String message) {
            if (this.message == null) {
                this.message = message;
            }
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return getCode() + " | " + getMessage() + " | " + getDescription();
        }

    }

}
