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
package org.wso2.carbon.identity.application.authenticator.biometric.servlet.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.equinox.http.helper.ContextPathServletAdaptor;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.HttpService;
import org.wso2.carbon.identity.application.authenticator.biometric.servlet.servlet.BiometricServlet;
import javax.servlet.Servlet;
import static org.wso2.carbon.identity.application.authenticator.biometric.servlet.BiometricServletConstants.BIOMETRIC_ENDPOINT;

/**
 * Service component class for the Biometric Servlet initialization.
 */
@Component(
        name = "identity.application.authenticator.biometric.servlet",
        immediate = true)
public class BiometricEndpointServiceComponent {

    private static final Log log = LogFactory.getLog(BiometricEndpointServiceComponent.class);
    private HttpService httpService;

    @Activate
    protected void activate(ComponentContext ctxt) {

        Servlet biometricServlet = new ContextPathServletAdaptor(new BiometricServlet(), BIOMETRIC_ENDPOINT);
        try {
            httpService.registerServlet(BIOMETRIC_ENDPOINT, biometricServlet, null, null);
            if (log.isDebugEnabled()) {
                log.debug("Biometric endpoint service component activated.");
            }
        } catch (Exception e) {
            log.error("Error when registering the biometric endpoint via the HTTP service.", e);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext ctxt) {

        httpService.unregister(BIOMETRIC_ENDPOINT);
        if (log.isDebugEnabled()) {
            log.debug("Biometric endpoint service component de-activated.");
        }
    }

    @Reference(
            name = "osgi.httpservice",
            service = org.osgi.service.http.HttpService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetHttpService")
    protected void setHttpService(HttpService httpService) {

        this.httpService = httpService;
    }

    protected void unsetHttpService(HttpService httpService) {

        this.httpService = null;
    }
}
