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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.exception.BiometricDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.exception.BiometricdeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.model.Device;
import org.wso2.carbon.identity.application.common.model.User;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

/**
 * The mock implementation of the  DeviecDAO interface .
 */
public class DeviceDAOMockImpl implements DeviceDAO {
    private static final Log log = LogFactory.getLog(DeviceDAOMockImpl.class);
    private static DeviceDAO dao;

    static {
        try {
            dao = new DeviceDAOMockImpl();
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to instantiate the DAO class");
        }
    }

    private HashMap<String, Device> deviceStore = new HashMap<>();
    private PrivateKey[] privateKeys = new PrivateKey[2];

    public static DeviceDAO getInstance() {
        return dao;
    }

    private DeviceDAOMockImpl() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        privateKeys[0] = keyPair.getPrivate();
        Device device1 = new Device("000AA11", "123", "My Iphone", "Iphone 8",
                "dsfsdf3dwawaddwa", Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()), new Date(),
                 new Date());

        keyPair = keyPairGen.generateKeyPair();
        privateKeys[1] = keyPair.getPrivate();
        Device device2 = new Device("000AA22", "124", "My Android", "Galaxy s10",
                "dsfsdf3dwasa3dwa", Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()), new Date(),
                new Date());
        deviceStore.put(device1.getDeviceId(), device1);
        deviceStore.put(device2.getDeviceId(), device2);

    }

    @Override
    public void registerDevice(Device device) throws BiometricDeviceHandlerClientException,
            BiometricdeviceHandlerServerException {
        String username = getAuthenticatedUser().getUserName();

        if (log.isDebugEnabled()) {
            log.debug("Registering new Biometric Authentication device for user " + username);
        }
        Date timestamp = new Date();
        device.setRegistrationTime(timestamp);
        device.setLastUsedTime(timestamp);
        device.setUserId(getUserIdFromUsername(username));
        deviceStore.put(device.getDeviceId(), device);
    }

    @Override
    public void unregisterDevice(String deviceId) throws BiometricDeviceHandlerClientException {
        if (log.isDebugEnabled()) {
            log.debug("Deleting device " + deviceId);
        }
        if (deviceStore.containsKey(deviceId)) {
            deviceStore.remove(deviceId);
        } else {
            throw new BiometricDeviceHandlerClientException("Device not found");
        }
    }

    @Override
    public Device getDevice(String deviceId) throws BiometricDeviceHandlerClientException {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving device " + deviceId);
        }
        if (StringUtils.isEmpty(deviceId)) {
            throw new BiometricDeviceHandlerClientException("Device ID not received ");
        }
        Device device =  deviceStore.get(deviceId);
        if (device != null) {
            return device;
        } else {
            throw new BiometricDeviceHandlerClientException("Device Not Found ");
        }

    }

    @Override
    public ArrayList<Device> listDevices() {
        ArrayList<Device> devices = new ArrayList<>();
        for (HashMap.Entry<String, Device> entry : deviceStore.entrySet()) {
            devices.add(entry.getValue());
        }
        return devices;
    }

    @Override
    public void deleteAllDevicesOfUser(int userId, String userStore) {

    }

    public void editDeviceName(String deviceId, String newDevicename) {
        if (log.isDebugEnabled()) {
            log.debug("Updateing device name of " + deviceId);
        }
        Device temp;
        if (deviceStore.containsKey(deviceId)) {
            temp = deviceStore.get(deviceId);
            temp.setDeviceName(newDevicename);
            deviceStore.put(deviceId, temp);
        } else {
            log.error("Device does not exist");
        }

    }

    private String getUserIdFromUsername(String username) throws BiometricdeviceHandlerServerException {
        try {
            AbstractUserStoreManager userStoreManager = (AbstractUserStoreManager) CarbonContext.
                    getThreadLocalCarbonContext().getUserRealm().getUserStoreManager();
            return userStoreManager.getUserIDFromUserName(username);
        } catch (UserStoreException e) {
            throw new BiometricdeviceHandlerServerException("Carbon Exception while retrieving User ID of " +
                    username, e);
        }
    }

    public User getAuthenticatedUser() throws BiometricDeviceHandlerClientException {
        User user = User.getUserFromUserName(CarbonContext.getThreadLocalCarbonContext().getUsername());
        if (user == null) {
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
