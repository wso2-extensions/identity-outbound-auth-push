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

package org.wso2.carbon.identity.application.authenticator.push.device.handler.cache;

import org.wso2.carbon.identity.application.authentication.framework.store.SessionDataStore;
import org.wso2.carbon.identity.application.common.cache.BaseCache;
import org.wso2.carbon.utils.CarbonUtils;

/**
 * Push Device handler cache to store interim data.
 */

public class RegistrationRequestChallengeCache extends
        BaseCache<PushDeviceHandlerCacheKey, RegistrationRequestChallengeCacheEntry> {

    private static final String REGISTRATION_REQUEST_CHALLENGE_CACHE = "RegistrationRequestChallengeCache";

    private static volatile RegistrationRequestChallengeCache cache;

    public RegistrationRequestChallengeCache() {

        super(REGISTRATION_REQUEST_CHALLENGE_CACHE, true);
    }

    public static RegistrationRequestChallengeCache getInstance() {

        CarbonUtils.checkSecurity();
        if (cache == null) {
            synchronized (RegistrationRequestChallengeCache.class) {
                cache = new RegistrationRequestChallengeCache();
            }
        }
        return cache;
    }

    private void storeToSessionStore(String id, RegistrationRequestChallengeCacheEntry entry) {

        SessionDataStore.getInstance().storeSessionData(id, REGISTRATION_REQUEST_CHALLENGE_CACHE, entry);
    }

    private RegistrationRequestChallengeCacheEntry getFromSessionStore(String id) {

        return (RegistrationRequestChallengeCacheEntry) SessionDataStore.getInstance().
                getSessionData(id, REGISTRATION_REQUEST_CHALLENGE_CACHE);
    }

    private void clearFromSessionStore(String id) {

        SessionDataStore.getInstance().clearSessionData(id, REGISTRATION_REQUEST_CHALLENGE_CACHE);
    }

    public void clearCacheEntryByRequestId(PushDeviceHandlerCacheKey key) {

        super.clearCacheEntry(key);
        clearFromSessionStore(key.getRequestId());
    }

    public void addToCacheByRequestId(PushDeviceHandlerCacheKey key,
                                      RegistrationRequestChallengeCacheEntry entry) {

        super.addToCache(key, entry);
        storeToSessionStore(key.getRequestId(), entry);

    }

    public RegistrationRequestChallengeCacheEntry getValueFromCacheByRequestId(PushDeviceHandlerCacheKey key) {

        return getFromSessionStore(key.getRequestId());
    }

}
