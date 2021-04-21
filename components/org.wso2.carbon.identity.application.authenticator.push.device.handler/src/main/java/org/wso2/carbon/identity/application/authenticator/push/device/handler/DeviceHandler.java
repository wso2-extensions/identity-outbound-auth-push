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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.Device;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.DiscoveryData;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.RegistrationRequest;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.user.api.UserStoreException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This interface contains the operations of the DeviceHandlerImpl class.
 */
public interface DeviceHandler {

    Device registerDevice(RegistrationRequest registrationRequest) throws
            IdentityException, SQLException, UserStoreException, JsonProcessingException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, UnsupportedEncodingException, InvalidKeySpecException;

    void unregisterDevice(String deviceId) throws PushDeviceHandlerClientException,
            PushDeviceHandlerServerException, SQLException;

    void editDeviceName(String deviceId, String newDeviceName) throws PushDeviceHandlerClientException,
            PushDeviceHandlerServerException, SQLException;

    Device getDevice(String deviceId) throws IOException, PushDeviceHandlerClientException, SQLException, PushDeviceHandlerServerException;

    ArrayList<Device> listDevices(String username, String userStore, String tenantDomain) throws PushDeviceHandlerServerException, PushDeviceHandlerClientException, SQLException, UserStoreException, IOException;

//    DiscoveryData getDiscoveryData(String username, String userStore, String tenantDomain);

    DiscoveryData getDiscoveryData();

    String getPublicKey(String deviceId) throws SQLException, IOException;;



}
