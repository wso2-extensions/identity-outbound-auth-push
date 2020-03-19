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

package org.wso2.carbon.identity.application.authenticator.biometric.device.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.exception.BiometricDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.exception.BiometricdeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.model.Device;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.model.DiscoveryData;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.model.RegistrationRequest;
import org.wso2.carbon.user.api.UserStoreException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This interface contains the operations of the DeviceHandlerImpl class.
 */
public interface DeviceHandler {

    Device registerDevice(RegistrationRequest registrationRequest) throws
            BiometricDeviceHandlerClientException, BiometricdeviceHandlerServerException, SQLException, UserStoreException, JsonProcessingException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, UnsupportedEncodingException;

    void unregisterDevice(String deviceId) throws BiometricDeviceHandlerClientException,
            BiometricdeviceHandlerServerException, SQLException;

    void editDeviceName(String deviceId, String newDeviceName) throws BiometricDeviceHandlerClientException,
            BiometricdeviceHandlerServerException, SQLException;

    Device getDevice(String deviceId) throws IOException, BiometricDeviceHandlerClientException, SQLException, BiometricdeviceHandlerServerException;

    ArrayList<Device> lisDevices() throws BiometricdeviceHandlerServerException, BiometricDeviceHandlerClientException, SQLException, UserStoreException, IOException;

    DiscoveryData getDiscoveryData();


}
