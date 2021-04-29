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
 */

package org.wso2.carbon.identity.application.authenticator.push.device.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.Device;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.RegistrationDiscoveryData;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.RegistrationRequest;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.user.api.UserStoreException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * This interface contains the operations of the DeviceHandlerImpl class.
 */
public interface DeviceHandler {

    /**
     * Register a new device
     *
     * @param registrationRequest HTTP request for device registration
     * @return registered device
     * @throws IdentityException
     * @throws UserStoreException
     * @throws JsonProcessingException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeySpecException
     */
    Device registerDevice(RegistrationRequest registrationRequest) throws
            IdentityException, UserStoreException, JsonProcessingException, InvalidKeyException,
            NoSuchAlgorithmException, SignatureException, UnsupportedEncodingException, InvalidKeySpecException;

    /**
     * Unregister a device
     *
     * @param deviceId ID of the device to unregister
     * @throws PushDeviceHandlerClientException
     * @throws PushDeviceHandlerServerException
     */
    void unregisterDevice(String deviceId) throws PushDeviceHandlerClientException, PushDeviceHandlerServerException;

    /**
     * Edit the name of a registered device
     *
     * @param deviceId      ID of the device to update the name of
     * @param newDeviceName New name for the device
     * @throws PushDeviceHandlerServerException
     */
    void editDeviceName(String deviceId, String newDeviceName) throws PushDeviceHandlerServerException;

    /**
     * Get a device by the device ID
     *
     * @param deviceId ID of the registered device
     * @return the device
     * @throws PushDeviceHandlerClientException
     * @throws PushDeviceHandlerServerException
     */
    Device getDevice(String deviceId) throws PushDeviceHandlerClientException, PushDeviceHandlerServerException;

    /**
     * Get the list of registered devices for a given user
     *
     * @param username     username of the authenticated user
     * @param userStore    userstore of the authenticated user
     * @param tenantDomain tenant domain of the authenticated user
     * @return list of devices for the authenticated user
     * @throws PushDeviceHandlerServerException
     * @throws PushDeviceHandlerClientException
     * @throws UserStoreException
     */
    List<Device> listDevices(String username, String userStore, String tenantDomain)
            throws PushDeviceHandlerServerException, PushDeviceHandlerClientException, UserStoreException;

    /**
     * Get discovery data for a new device registration
     *
     * @return discovery data
     */
    RegistrationDiscoveryData getRegistrationDiscoveryData();

    /**
     * Get public key for registered device
     *
     * @param deviceId ID of the registered device
     * @return Public key string
     * @throws PushDeviceHandlerServerException
     */
    String getPublicKey(String deviceId) throws PushDeviceHandlerServerException;

}
