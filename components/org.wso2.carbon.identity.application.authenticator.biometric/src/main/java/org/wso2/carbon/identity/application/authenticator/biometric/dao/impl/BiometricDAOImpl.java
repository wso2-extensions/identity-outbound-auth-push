/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.identity.application.authenticator.biometric.dao.impl;

import org.wso2.carbon.identity.application.authenticator.biometric.dao.BiometricDAO;

import java.util.HashMap;

/**
 * performs DAO operations related to Biometric device store which stores the device IDs against the usernames.
 */
public class BiometricDAOImpl implements BiometricDAO {
    private static BiometricDAOImpl biometricDAO = new BiometricDAOImpl();
    private HashMap<String, String> deviceIDStore = new HashMap<>();

    private BiometricDAOImpl() {

        deviceIDStore.put("dewni", "ca0OWsg-30c:APA91bHQj3LRdOt7BgQJsOGbz_uWBFe8BtBLS0WSXZFNnVj6BZkU7PM__" +
                "bHuoZXE6z_mzsjdZaKMerHaILfsf2jalgpndp67b5Vt9xvG0lODmksCq-Nk5N8pdIv1DRHJkVZGKygFcnmw");
        deviceIDStore.put("chamodi", "cMXgOC6PIFA:APA91bHbV-ZT6JJnqPtt_0jCtImNFtEFefhY3FAck83eZkUB" +
                "nRmUevrgLbo3G1iC8Xf8dpH3mQ0kVhnJTHnoX_8UocAc5Y1-zbEbTUYQMdFNFruT77KX8skRRF7XY_X8C4Wlo3nZmWAu");
        deviceIDStore.put("yasara", "cbK57eqxofQ:APA91bEJln5WTOzM3hqhDlwPVlj3FXbBaUqQRgRwNGpTSOm" +
                "YcSXSPaazm4WBeVGYnPcwVJc6FtxzvT4QsRV83akYaUXk6fKEsh0tFs9n4ZEiy_TKFUWPoLPnKkkWj_52Z5x1X788LCn9");
    }

    public static BiometricDAOImpl getInstance() {
        return biometricDAO;
    }

    @Override
    public void addDeviceID(String username, String deviceID) {
        deviceIDStore.put(username, deviceID);
    }

    @Override
    public void updateDeviceID(String username, String deviceID) {
        deviceIDStore.replace(username, deviceID);
    }

    @Override
    public void deleteDeviceID(String username) {
        deviceIDStore.remove(username);
    }

    @Override
    public String getDeviceID(String username) {
        return deviceIDStore.get(username);
    }
}
