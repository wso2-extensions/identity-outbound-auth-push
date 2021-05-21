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
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandler;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.impl.DeviceHandlerImpl;

import java.util.Hashtable;

/**
 * This class activates the device handler bundle.
 */
@Component(
        name = "org.wso2.carbon.identity.application.authenticator.push.device.handler",
        immediate = true)
public class PushDeviceHandlerServiceComponent {

    private static final Log log = LogFactory.getLog(PushDeviceHandlerServiceComponent.class);

    @Activate
    protected void activate(ComponentContext ctxt) {

        try {
            DeviceHandler deviceHandler = new DeviceHandlerImpl();
            Hashtable<String, String> props = new Hashtable<>();
            ctxt.getBundleContext().registerService(DeviceHandler.class.getName(),
                    deviceHandler, props);
            if (log.isDebugEnabled()) {
                log.debug("Push device handler service component is activated.");
            }

        } catch (Exception e) {
            log.fatal("Error while activating the push authenticator ", e);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext ctxt) {

        if (log.isDebugEnabled()) {
            log.debug("Push device handler service component is deactivated.");
        }
    }

}
