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

package org.wso2.carbon.identity.application.authenticator.biometric.servlet.store.impl;

import org.wso2.carbon.identity.application.authenticator.biometric.servlet.store.BiometricDataStore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Updates a hash-map which stores session data key against signed challenge from the mobile application.
 */
public class BiometricDataStoreImpl implements BiometricDataStore, Serializable {

    private static final long serialVersionUID = 8385881451715660472L;
    private static BiometricDataStoreImpl biometricDataStoreInstance = new BiometricDataStoreImpl();
    private Map<String, String> biometricDataStore = new HashMap<>();


    public static BiometricDataStoreImpl getInstance() {
        return biometricDataStoreInstance;
    }
    @Override
    public String getSignedChallenge(String sessionDataKey) {

        return biometricDataStore.get(sessionDataKey);
    }

    @Override
    public void addBiometricData(String sessionDataKey, String signedChallenge) {

        biometricDataStore.put(sessionDataKey, signedChallenge);
    }

    @Override
    public void removeBiometricData(String sessionDataKey) {

        biometricDataStore.remove(sessionDataKey);
    }
}
