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

package org.wso2.carbon.identity.application.authenticator.push.cache;

import org.wso2.carbon.identity.application.authentication.framework.store.SessionDataStore;
import org.wso2.carbon.identity.application.common.cache.BaseCache;
import org.wso2.carbon.utils.CarbonUtils;

public class AuthContextCache extends
        BaseCache<AuthContextcacheKey, AuthContextCacheEntry> {

    private static final String AUTH_CONTEXT_CACHE_NAME = "AuthContextCache";
    private static volatile AuthContextCache cache;

    public AuthContextCache(String cacheName) {

        super(cacheName);
    }

    public AuthContextCache() {

        super(AUTH_CONTEXT_CACHE_NAME, true);
    }

    public static AuthContextCache getInstance() {

        CarbonUtils.checkSecurity();
        if (cache == null) {
            synchronized (AuthContextCache.class) {
                cache = new AuthContextCache();
            }
        }
        return cache;
    }

    private void storeToSessionStore(String id, AuthContextCacheEntry entry) {

        SessionDataStore.getInstance().storeSessionData(id, AUTH_CONTEXT_CACHE_NAME, entry);
    }

    private AuthContextCacheEntry getFromSessionStore(String id) {

        return (AuthContextCacheEntry) SessionDataStore.getInstance().
                getSessionData(id, AUTH_CONTEXT_CACHE_NAME);
    }

    private void clearFromSessionStore(String id) {

        SessionDataStore.getInstance().clearSessionData(id, AUTH_CONTEXT_CACHE_NAME);
    }

    public void clearCacheEntryByRequestId(AuthContextcacheKey key) {

        super.clearCacheEntry(key);
        clearFromSessionStore(key.getRequestId());
    }

    public void addToCacheByRequestId(AuthContextcacheKey key,
                                      AuthContextCacheEntry entry) {

        super.addToCache(key, entry);
        storeToSessionStore(key.getRequestId(), entry);

    }

    public AuthContextCacheEntry getValueFromCacheByRequestId(AuthContextcacheKey key) {

        return getFromSessionStore(key.getRequestId());
    }
}
