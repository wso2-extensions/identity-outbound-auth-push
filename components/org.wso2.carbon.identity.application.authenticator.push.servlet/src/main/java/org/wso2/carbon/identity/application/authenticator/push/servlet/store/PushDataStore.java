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

package org.wso2.carbon.identity.application.authenticator.push.servlet.store;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Updates a hash-map which stores the status of the authentication request.
 */
public class PushDataStore implements Serializable {

    private static final long serialVersionUID = 8385881451715660472L;
    private static final PushDataStore pushDataStoreInstance = new PushDataStore();
    private final Map<String, String> pushDataStore = new HashMap<>();

    private PushDataStore() {

    }

    public static PushDataStore getInstance() {

        return pushDataStoreInstance;
    }

    /**
     * Returns the authentication status stored against the session data key in push data store.
     *
     * @param sessionDataKey Unique ID for the session
     * @return Authentication status
     */
    public String getAuthStatus(String sessionDataKey) {

        return pushDataStore.get(sessionDataKey + "status");
    }

    /**
     * Adds a new record of session data key against auth status to the push data store.
     *
     * @param sessionDataKey Unique ID for the session
     * @param authStatus     Authentication status
     */
    public void updateAuthStatus(String sessionDataKey, String authStatus) {

        pushDataStore.put(sessionDataKey + "status", authStatus);
    }

    /**
     * Removes the record with the given session data key in push data store.
     *
     * @param sessionDataKey Unique ID for the session
     */
    public void removePushData(String sessionDataKey) {

        pushDataStore.remove(sessionDataKey);
    }
}
