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

package org.wso2.carbon.identity.application.authenticator.push.common.cache;

import org.wso2.carbon.identity.application.authentication.framework.store.SessionDataStore;
import org.wso2.carbon.identity.application.common.cache.BaseCache;

/**
 * Class handling push authentication cache.
 */
public class PushAuthContextCache extends
        BaseCache<PushAuthContextCacheKey, PushAuthContextCacheEntry> {

    private static final String PUSH_AUTH_CONTEXT_CACHE = "PushAuthContextCache";
    private static volatile PushAuthContextCache cache;

    private PushAuthContextCache() {

        super(PUSH_AUTH_CONTEXT_CACHE, true);
    }

    public static PushAuthContextCache getInstance() {

        if (cache == null) {
            synchronized (PushAuthContextCache.class) {
                cache = new PushAuthContextCache();
            }
        }
        return cache;
    }

    /**
     * Store authentication context to the session data store
     *
     * @param id SessionDataKey for the session
     * @param entry PushAuthContextCacheEntry containing push authentication context
     */
    private void storeToSessionStore(String id, PushAuthContextCacheEntry entry) {

        SessionDataStore.getInstance().storeSessionData(id, PUSH_AUTH_CONTEXT_CACHE, entry);
    }

    /**
     * Gets the push authentication context from SessionDataStore by the SessionDataKey
     *
     * @param id SessionDataKey used as the ID
     * @return Push authentication context
     */
    private PushAuthContextCacheEntry getFromSessionStore(String id) {

        return (PushAuthContextCacheEntry) SessionDataStore.getInstance().getSessionData(id, PUSH_AUTH_CONTEXT_CACHE);
    }

    /**
     * Remove cached authentication context by SessionDataKey
     *
     * @param id SessionDataKey for the session
     */
    private void clearFromSessionStore(String id) {

        SessionDataStore.getInstance().clearSessionData(id, PUSH_AUTH_CONTEXT_CACHE);
    }

    /**
     * Clear stored cache under the SessionDataKey
     *
     * @param key PushAuthenticationContextKey with SessionDataKey
     */
    public void clearCacheEntryByRequestId(PushAuthContextCacheKey key) {

        super.clearCacheEntry(key);
        clearFromSessionStore(key.getRequestId());
    }

    /**
     * Add the authentication context to cache by the SessionDataKey
     *
     * @param key PushAuthenticationContextKey with SessionDataKey
     * @param entry PushAuthenticationCacheEntry containing authentication context
     */
    public void addToCacheByRequestId(PushAuthContextCacheKey key,
                                      PushAuthContextCacheEntry entry) {

        super.addToCache(key, entry);
        storeToSessionStore(key.getRequestId(), entry);

    }

    /**
     * Gets the push authentication context from cache by the SessionDataKey
     *
     * @param key PushAuthenticationContextKey with SessionDataKey
     * @return PushAuthenticationCacheEntry containing authentication context
     */
    public PushAuthContextCacheEntry getValueFromCacheByRequestId(PushAuthContextCacheKey key) {

        PushAuthContextCacheEntry cacheEntry = super.getValueFromCache(key);
        if (cacheEntry == null) {
            cacheEntry = getFromSessionStore(key.getRequestId());
        }
        return cacheEntry;
    }
}
