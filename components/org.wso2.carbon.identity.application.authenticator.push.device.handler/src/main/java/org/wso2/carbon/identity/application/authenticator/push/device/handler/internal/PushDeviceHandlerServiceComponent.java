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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.wso2.carbon.identity.user.store.configuration.listener.UserStoreConfigListener;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * This class activates the device handler bundle.
 */
@Component(
        name = "identity.push.device.handler",
        immediate = true
)
public class PushDeviceHandlerServiceComponent implements BundleActivator {
    private static final Log log = LogFactory.getLog(PushDeviceHandlerServiceComponent.class);
    private static RealmService realmService;

    protected void setRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("Setting the Realm Service.");
        }
        PushDeviceHandlerServiceComponent.realmService = realmService;
    }

    protected void unsetRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("UnSetting the Realm Service");
        }
        PushDeviceHandlerServiceComponent.realmService = null;
    }

    public static RealmService getRealmService() {

        return realmService;
    }

    @Activate
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        try {
            bundleContext.registerService(UserStoreConfigListener.class.getName(),
                    new UserStoreConfigListenerImpl(), null);
            log.debug("Activating Push Device Handler bundle...");

        } catch (Exception e) {
            log.error("Error registering UserStoreConfigListener ", e);
        }
    }

    @Deactivate
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Deactivating Push Device Handler bundle...");
        }
    }
}
