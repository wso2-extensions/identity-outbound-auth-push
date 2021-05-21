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

package org.wso2.carbon.identity.application.authenticator.push.device.handler.dao;

import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.Device;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * This class defines the database related operations.
 */
public interface DeviceDAO {

    /**
     * Registers a new device by storing in the database.
     *
     * @param device Object containing data for the device
     * @throws SQLException
     */
    void registerDevice(Device device) throws SQLException;

    /**
     * Unregisters a device by removing it from the database.
     *
     * @param deviceId Unique ID for the device to be deleted
     * @throws SQLException
     */
    void unregisterDevice(String deviceId) throws SQLException;

    /**
     * Update the properties of a given device.
     *
     * @param deviceId      Unique ID for the device to be deleted
     * @param updatedDevice New name for the given device
     * @throws SQLException
     */
    void editDevice(String deviceId, Device updatedDevice) throws SQLException;

    /**
     * Get the device object using the device class.
     *
     * @param deviceId Unique ID of the required device
     * @return
     * @throws SQLException
     */
    Optional<Device> getDevice(String deviceId) throws SQLException;

    /**
     * Get the list of devices for the user using the username.
     *
     * @param userId Unique ID of the user requesting the device list
     * @return
     * @throws SQLException
     */
    List<Device> listDevices(String userId) throws SQLException;

    /**
     * Delete all the devices for the given user from the database.
     *
     * @param userId Unique ID to identify the user
     */
    void deleteAllDevicesOfUser(String userId) throws SQLException;

    /**
     * Get the public key for a specific device from the database.
     *
     * @param deviceId Unique ID to identify the device
     * @return Public Key string
     * @throws SQLException
     */
    Optional<String> getPublicKey(String deviceId) throws SQLException;

}
