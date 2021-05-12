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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandlerConstants;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.Device;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class implements DeviceDAO interface .
 */
public class DeviceDAOImpl implements DeviceDAO {

    private static final Log log = LogFactory.getLog(DeviceDAOImpl.class);
    private static DeviceDAO dao = new DeviceDAOImpl();

    private DeviceDAOImpl() {

    }

    public static DeviceDAO getInstance() {

        return dao;
    }

    @Override
    public void registerDevice(Device device) throws SQLException {

        Connection connection = IdentityDatabaseUtil.getDBConnection(true);
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQueries.REGISTER_DEVICE);
            preparedStatement.setString(1, device.getDeviceId());
            preparedStatement.setString(2, device.getUserId());
            preparedStatement.setString(3, device.getDeviceName());
            preparedStatement.setString(4, device.getDeviceModel());
            preparedStatement.setString(5, device.getPushId());
            preparedStatement.setString(6, device.getPublicKey());
            preparedStatement.setTimestamp(7, new Timestamp(new Date().getTime()));
            preparedStatement.setTimestamp(8, new Timestamp(new Date().getTime()));
            preparedStatement.execute();
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);
        }
    }

    @Override
    public void unregisterDevice(String deviceId) throws SQLException {

        Connection connection = IdentityDatabaseUtil.getDBConnection(true);
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQueries.UNREGISTER_DEVICE);
            preparedStatement.setString(1, deviceId);
            preparedStatement.execute();
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);
        }
    }

    @Override
    public void editDevice(String deviceId, Device updatedDevice) throws SQLException {

        Connection connection = IdentityDatabaseUtil.getDBConnection(true);
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQueries.EDIT_DEVICE);
            preparedStatement.setString(1, updatedDevice.getDeviceName());
            preparedStatement.setString(2, updatedDevice.getPushId());
            preparedStatement.setString(3, deviceId);
            preparedStatement.execute();
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);
        }

    }

    @Override
    public Device getDevice(String deviceId) throws PushDeviceHandlerServerException, SQLException {

        Connection connection = IdentityDatabaseUtil.getDBConnection(true);
        PreparedStatement preparedStatement = null;
        Device device = new Device();
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQueries.GET_DEVICE);
            preparedStatement.setString(1, deviceId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                device.setDeviceId(resultSet.getString(1));
                device.setDeviceName(resultSet.getString(2));
                device.setDeviceModel(resultSet.getString(3));
                device.setPushId(resultSet.getString(4));
                device.setPublicKey(resultSet.getString(5));
                device.setRegistrationTime(timestampToDate(resultSet.getTimestamp(6)));
                device.setLastUsedTime(timestampToDate(resultSet.getTimestamp(7)));
            } else {
                IdentityDatabaseUtil.closeAllConnections(connection, resultSet, preparedStatement);
                String errorMessage =
                        String.format("The requested device: %s is not registered in the system.", deviceId);
                throw new PushDeviceHandlerServerException(errorMessage);
            }
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, resultSet, preparedStatement);
        }

        return device;
    }

    @Override
    public List<Device> listDevices(String userId) throws SQLException {

        List<Device> devices = new ArrayList<>();
        Connection connection = IdentityDatabaseUtil.getDBConnection(true);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            Device device;
            preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQueries.LIST_DEVICES);
            preparedStatement.setString(1, userId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                device = new Device();
                device.setDeviceId(resultSet.getString(1));
                device.setDeviceName(resultSet.getString(2));
                device.setDeviceModel(resultSet.getString(3));
                device.setRegistrationTime(timestampToDate(resultSet.getTimestamp(4)));
                device.setLastUsedTime(timestampToDate(resultSet.getTimestamp(5)));
                devices.add(device);
            }
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, resultSet, preparedStatement);
        }

        return devices;
    }

    @Override
    public void deleteAllDevicesOfUser(int tenant, String userStore) {

    }

    @Override
    public String getPublicKey(String deviceId) throws SQLException {

        Connection connection = IdentityDatabaseUtil.getDBConnection(true);
        PreparedStatement preparedStatement = null;
        String publicKey = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQueries.GET_PUBLIC_KEY);
            preparedStatement.setString(1, deviceId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                publicKey = (resultSet.getString(1));
            }
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, resultSet, preparedStatement);
        }

        return publicKey;
    }

    /**
     * Convert timestamp to date type
     *
     * @param timestamp Timestamp object
     * @return Date object
     */
    private Date timestampToDate(Timestamp timestamp) {

        return new Date(timestamp.getTime());
    }

    /**
     * Convert date to timestamp type
     *
     * @param date Date object
     * @return Timestamp object
     */
    private Timestamp dateToTimestamp(Date date) {

        return new Timestamp(date.getTime());
    }
}
