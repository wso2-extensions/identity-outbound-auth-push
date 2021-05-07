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
package org.wso2.carbon.identity.application.authenticator.push.servlet.internal;

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
import org.wso2.carbon.identity.application.authenticator.push.servlet.PushServletConstants;
import org.wso2.carbon.identity.application.authenticator.push.servlet.servlet.PushAuthCheckServlet;
import org.wso2.carbon.identity.application.authenticator.push.servlet.servlet.PushServlet;

import javax.servlet.Servlet;

/**
 * Service component class for the Push Servlet initialization.
 */
@Component(
        name = "identity.application.authenticator.push.servlet",
        immediate = true)
public class PushEndpointServiceComponent {

    private static final Log log = LogFactory.getLog(PushEndpointServiceComponent.class);
    private HttpService httpService;

    @Activate
    protected void activate(ComponentContext ctxt) {

        Servlet pushServlet = new ContextPathServletAdaptor(new PushServlet(),
                PushServletConstants.PUSH_AUTH_ENDPOINT);
        Servlet statusServlet = new ContextPathServletAdaptor(new PushAuthCheckServlet(),
                PushServletConstants.PUSH_AUTH_STATUS_ENDPOINT);
        Servlet sendServlet = new ContextPathServletAdaptor(new PushAuthCheckServlet(),
                PushServletConstants.PUSH_AUTH_SEND_ENDPOINT);

        try {
            httpService.registerServlet(PushServletConstants.PUSH_AUTH_ENDPOINT, pushServlet,
                    null, null);
            httpService.registerServlet(PushServletConstants.PUSH_AUTH_STATUS_ENDPOINT, statusServlet,
                    null, null);
            httpService.registerServlet(PushServletConstants.PUSH_AUTH_SEND_ENDPOINT, sendServlet,
                    null, null);
            if (log.isDebugEnabled()) {
                log.debug("Push endpoint service component activated."
                        + "\n Authentication endpoint    : " + PushServletConstants.PUSH_AUTH_ENDPOINT
                        + "\n Check status endpoint      : " + PushServletConstants.PUSH_AUTH_STATUS_ENDPOINT
                        + "\n Send auth request endpoint : " + PushServletConstants.PUSH_AUTH_SEND_ENDPOINT);
            }
        } catch (Exception e) {
            log.error("Error when registering the push endpoint via the HTTP service.", e);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext ctxt) {

        httpService.unregister(PushServletConstants.PUSH_AUTH_ENDPOINT);
        httpService.unregister(PushServletConstants.PUSH_AUTH_STATUS_ENDPOINT);
        httpService.unregister(PushServletConstants.PUSH_AUTH_SEND_ENDPOINT);
        if (log.isDebugEnabled()) {
            log.debug("Push endpoint service component de-activated.");
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
