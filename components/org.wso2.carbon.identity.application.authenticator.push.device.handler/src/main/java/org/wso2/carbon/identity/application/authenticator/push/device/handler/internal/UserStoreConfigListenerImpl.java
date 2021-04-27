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

package org.wso2.carbon.identity.application.authenticator.push.device.handler.internal;


import org.wso2.carbon.identity.user.store.configuration.listener.AbstractUserStoreConfigListener;
import org.wso2.carbon.user.api.UserStoreException;

/**
 *Operations to be carried out if changes are made to the userstore .
 */
public class UserStoreConfigListenerImpl extends AbstractUserStoreConfigListener {


    @Override
    public void onUserStorePreDelete(int tenantId, String userStoreName) throws UserStoreException {
        //operations to perform prior to deleting a user store
    }

}
