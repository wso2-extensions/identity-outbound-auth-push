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

package org.wso2.carbon.identity.application.authenticator.biometric.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.identity.application.authenticator.biometric.BiometricAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.ApplicationAuthenticator;

import java.util.Hashtable;

/**
 * @scr.component name="identity.application.authenticator.biometric.component" immediate="true"
 */
public class BiometricAuthenticatorServiceComponent {

    private static Log log = LogFactory.getLog(BiometricAuthenticatorServiceComponent.class);

    protected void activate(ComponentContext ctxt) {
        try {
            BiometricAuthenticator authenticator = new BiometricAuthenticator();
            Hashtable<String, String> props = new Hashtable<String, String>();
            ctxt.getBundleContext().registerService(ApplicationAuthenticator.class.getName(),
                    authenticator, props);
            if (log.isDebugEnabled()) {
                log.debug("biometric authenticator is activated");
            }
        } catch (Throwable e) {
            log.fatal("Error while activating the biometric authenticator ", e);
        }
    }

    /**
     *
     */
    protected void deactivate(ComponentContext ctxt) {
        if (log.isDebugEnabled()) {
            log.debug("biometric authenticator is deactivated");
        }
    }
}
