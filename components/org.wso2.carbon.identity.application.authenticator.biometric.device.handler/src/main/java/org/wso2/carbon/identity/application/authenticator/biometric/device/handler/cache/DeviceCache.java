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

package org.wso2.carbon.identity.application.authenticator.biometric.device.handler.cache;

import org.wso2.carbon.identity.application.authentication.framework.store.SessionDataStore;
import org.wso2.carbon.identity.application.common.cache.BaseCache;
import org.wso2.carbon.utils.CarbonUtils;
/**
 * Biometric device cache.
 */
public class DeviceCache extends
        BaseCache<BiometricDeviceHandlerCacheKey, DeviceCacheEntry> {
    private static final String BIOMETRIC_DEVICE_CACHE_NAME = "BiometricDeviceCache";

    private static volatile DeviceCache cache;


    public DeviceCache() {
        super(BIOMETRIC_DEVICE_CACHE_NAME, true);
    }

    public static DeviceCache getInstance() {
        CarbonUtils.checkSecurity();
        if (cache == null) {
            synchronized (RegistrationRequestChallengeCache.class) {
                cache = new DeviceCache();
            }
        }
        return cache;
    }

    private void storeToSessionStore(String id, DeviceCacheEntry entry) {
        SessionDataStore.getInstance().storeSessionData(id, BIOMETRIC_DEVICE_CACHE_NAME, entry);
    }

    private DeviceCacheEntry getFromSessionStore(String id) {
        return (DeviceCacheEntry) SessionDataStore.getInstance().
                getSessionData(id, BIOMETRIC_DEVICE_CACHE_NAME);
    }

    private void clearFromSessionStore(String id) {
        SessionDataStore.getInstance().clearSessionData(id, BIOMETRIC_DEVICE_CACHE_NAME);
    }

    public void clearCacheEntryByRequestId(BiometricDeviceHandlerCacheKey key) {
        super.clearCacheEntry(key);
        clearFromSessionStore(key.getRequestId());
    }

    public void addToCacheByRequestId(BiometricDeviceHandlerCacheKey key,
                                      DeviceCacheEntry entry) {
        super.addToCache(key, entry);
        storeToSessionStore(key.getRequestId(), entry);

    }

    public DeviceCacheEntry getValueFromCacheByRequestId(BiometricDeviceHandlerCacheKey key) {

        return getFromSessionStore(key.getRequestId());
    }
}
