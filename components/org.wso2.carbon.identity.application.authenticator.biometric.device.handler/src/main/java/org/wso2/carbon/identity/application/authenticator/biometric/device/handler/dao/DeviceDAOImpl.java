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

package org.wso2.carbon.identity.application.authenticator.biometric.device.handler.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.DeviceHandlerConstants;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.exception.BiometricDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.exception.BiometricdeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.model.Device;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.util.BiometricdeviceHandlerUtil;
import org.wso2.carbon.identity.application.common.model.User;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class implements DeviecDAO interface .
 */
public class DeviceDAOImpl implements DeviceDAO {
    private static final Log log = LogFactory.getLog(DeviceDAOImpl.class);
    private static DeviceDAO dao = new DeviceDAOImpl();

    private DeviceDAOImpl()  {

    }

    public static DeviceDAO getInstance() {
        return dao;
    }

    public void registerDevice(Device device) throws BiometricDeviceHandlerClientException, SQLException,
            JsonProcessingException, UserStoreException {
        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        String username = getAuthenticatedUser().getUserName();
        device.setUserId(getUserIdFromUsername(username));
        preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQUERIES.
                REGISTER_DEVICE
        );
        preparedStatement.setString(1, device.getDeviceId());
        preparedStatement.setString(2, device.getUserId());
        preparedStatement.setString(3, device.getDeviceName());
        preparedStatement.setString(4, device.getDeviceModel());
        preparedStatement.setString(5, device.getPushId());
        preparedStatement.setString(6, BiometricdeviceHandlerUtil.objectToJson(device.getPublicKey()));
        preparedStatement.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
        preparedStatement.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
        preparedStatement.execute();
        if (!connection.getAutoCommit()) {
            connection.commit();
        }
        IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);
    }

    public void unregisterDevice(String deviceId) throws BiometricdeviceHandlerServerException, SQLException {
        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQUERIES.UNREGISTER_DEVICE);
        preparedStatement.setString(1, deviceId);
        preparedStatement.execute();
        IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);
    }

    public void editDeviceName(String deviceId, String newDevicename) throws SQLException {
        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQUERIES.EDIT_DEVICE_NAME);
        preparedStatement.setString(1, newDevicename);
        preparedStatement.setString(2, deviceId);
        preparedStatement.execute();


        IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);

    }

    public Device getDevice(String deviceId) throws BiometricdeviceHandlerServerException, SQLException,
            IOException {

        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        Device device = new Device();

        preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQUERIES.GET_DEVICE);
        preparedStatement.setString(1, deviceId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet == null) {
            log.error("The requested device is not registered in the system");
            throw new BiometricdeviceHandlerServerException("Device Not found.");
        } else {
            while (resultSet.next()) {
                device.setDeviceId(resultSet.getString(1));
                device.setUserId(resultSet.getString(2));
                device.setDeviceName(resultSet.getString(3));
                device.setDeviceModel(resultSet.getString(4));
                device.setPushId(resultSet.getString(5));
                device.setPublicKey(resultSet.getString(6));
                device.setRegistrationTime(resultSet.getTimestamp(7));
                device.setLastUsedTime(resultSet.getTimestamp(8));
            }
        }

        IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);

        return device;
    }

    public ArrayList<Device> listDevices() throws BiometricdeviceHandlerServerException,
            BiometricDeviceHandlerClientException, SQLException, IOException, UserStoreException {
        String userId = getUserIdFromUsername(getAuthenticatedUser().getUserName());
        ArrayList<Device> devices = new ArrayList<>();
        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        Device device = new Device();
        preparedStatement = connection.prepareStatement(DeviceHandlerConstants.SQLQUERIES.LIST_DEVICES);
        preparedStatement.setString(1, userId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet != null) {
            while (resultSet.next()) {
                device.setDeviceId(resultSet.getString(1));
                device.setUserId(resultSet.getString(2));
                device.setDeviceName(resultSet.getString(3));
                device.setDeviceModel(resultSet.getString(4));
                device.setPushId(resultSet.getString(5));
                device.setPublicKey(resultSet.getString(6));
                device.setRegistrationTime(resultSet.getTimestamp(7));
                device.setLastUsedTime(resultSet.getTimestamp(8));
            }
        }
        IdentityDatabaseUtil.closeAllConnections(connection, null, preparedStatement);

        return devices;
    }

    @Override
    public void deleteAllDevicesOfUser(int tenant, String userStore) {

    }

    private String getUserIdFromUsername(String username) throws UserStoreException {
        AbstractUserStoreManager userStoreManager = (AbstractUserStoreManager) CarbonContext.
                getThreadLocalCarbonContext().getUserRealm().getUserStoreManager();
        return userStoreManager.getUserIDFromUserName(username);

    }

    public User getAuthenticatedUser() throws BiometricDeviceHandlerClientException {
        User user = User.getUserFromUserName(CarbonContext.getThreadLocalCarbonContext().getUsername());
        if (user.getUserName() == null) {
            log.error("Error while retrieving data of the user");
            throw new BiometricDeviceHandlerClientException("Authenticated user could not be retrieved");
        }
        user.setTenantDomain(CarbonContext.getThreadLocalCarbonContext().getTenantDomain());
        return user;
    }

    private Timestamp getDate() {
        Date date = new Date();
        return new Timestamp(date.getTime());
    }

    private Date timeStampToDate(Timestamp timeStamp) {
        return new Date(timeStamp.getTime());
    }


}
