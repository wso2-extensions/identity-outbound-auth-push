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
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandlerConstants;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.Device;
import org.wso2.carbon.identity.application.common.model.User;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;

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
        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQUERIES.REGISTER_DEVICE);
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
        IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);
    }

    @Override
    public void unregisterDevice(String deviceId) throws SQLException {
        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQUERIES.UNREGISTER_DEVICE);
        preparedStatement.setString(1, deviceId);
        preparedStatement.execute();
        IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);
    }

    @Override
    public void editDeviceName(String deviceId, String newDeviceName) throws SQLException {
        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQUERIES.EDIT_DEVICE_NAME);
        preparedStatement.setString(1, newDeviceName);
        preparedStatement.setString(2, deviceId);
        preparedStatement.execute();


        IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);

    }

    @Override
    public Device getDevice(String deviceId) throws PushDeviceHandlerServerException, SQLException {

        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        Device device = new Device();

        preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQUERIES.GET_DEVICE);
        preparedStatement.setString(1, deviceId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet != null) {
            if (resultSet.next()) {
                device.setDeviceId(resultSet.getString(1));
                device.setDeviceName(resultSet.getString(2));
                device.setDeviceModel(resultSet.getString(3));
                device.setPushId(resultSet.getString(4));
                device.setPublicKey(resultSet.getString(5));
                device.setRegistrationTime(timestampToDate(resultSet.getTimestamp(6)));
                device.setLastUsedTime(timestampToDate(resultSet.getTimestamp(7)));
            } else {
                log.error("The requested device is not registered in the system");
                IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);
                throw new PushDeviceHandlerServerException("Device Not found.");
            }
        }


        IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);

        return device;
    }

    @Override
    public List<Device> listDevices(String username, String userStore, String tenantDomain)
            throws SQLException, UserStoreException {
        String userId = getUserIdFromUsername(username);
        List<Device> devices = new ArrayList<>();
        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        Device device = null;
        preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQUERIES.LIST_DEVICES);
        preparedStatement.setString(1, userId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet != null) {
            while (resultSet.next()) {
                device = new Device();
                device.setDeviceId(resultSet.getString(1));
                device.setDeviceName(resultSet.getString(2));
                device.setDeviceModel(resultSet.getString(3));
                device.setRegistrationTime(timestampToDate(resultSet.getTimestamp(4)));
                device.setLastUsedTime(timestampToDate(resultSet.getTimestamp(5)));
                devices.add(device);
            }
        }
        IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);

        return devices;
    }

    @Override
    public void deleteAllDevicesOfUser(int tenant, String userStore) {

    }

    @Override
    public String getPublicKey(String deviceId) throws SQLException {
        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        String publicKey = null;

        preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQUERIES.GET_PUBLIC_KEY);
        preparedStatement.setString(1, deviceId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet != null) {
            if (resultSet.next()) {
                publicKey = (resultSet.getString(1));
            }

        }


        IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);
        return publicKey;
    }

    private String getUserIdFromUsername(String username) throws UserStoreException {
        AbstractUserStoreManager userStoreManager = (AbstractUserStoreManager) CarbonContext.
                getThreadLocalCarbonContext().getUserRealm().getUserStoreManager();
        return userStoreManager.getUserIDFromUserName(username);

    }

    public User getAuthenticatedUser() throws PushDeviceHandlerClientException {
        User user = User.getUserFromUserName(CarbonContext.getThreadLocalCarbonContext().getUsername());
        if (user.getUserName() == null) {
            log.error("Error while retrieving data of the user");
            throw new PushDeviceHandlerClientException("Authenticated user could not be retrieved");
        }
        user.setTenantDomain(CarbonContext.getThreadLocalCarbonContext().getTenantDomain());
        return user;
    }

    private Date timestampToDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }

    private Timestamp dateToTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }
}
