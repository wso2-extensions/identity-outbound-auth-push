/*
 * Copyright (c) 2005, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
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
package org.wso2.carbon.identity.sso.saml.internal;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.equinox.http.helper.ContextPathServletAdaptor;
import org.osgi.service.component.ComponentContext;
//import org.osgi.service.component.annotations.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.HttpService;
import org.wso2.carbon.identity.sso.saml.servlet.SAMLBiometricServlet;
import javax.servlet.Servlet;


/**
 * Service component class for the SAML SSO service.
 */
@Component(
         name = "identity.sso.saml.component",
         immediate = true)
public class IdentitySAMLSSOServiceComponent {

    private static final Log log = LogFactory.getLog(IdentitySAMLSSOServiceComponent.class);
    private HttpService httpService;

    @Activate
    protected void activate(ComponentContext ctxt) {




        //Register SAML Biometric servlet
        Servlet samlBiometricServlet = new ContextPathServletAdaptor(new SAMLBiometricServlet(), "/samlbiomtriccheck");
        try {
            httpService.registerServlet("/samlbiomtriccheck", samlBiometricServlet, null, null);
        } catch (Exception e) {
            String errMsg = "Error when registering SAML SSO Servlet via the HttpService.";
            log.error(errMsg, e);
            throw new RuntimeException(errMsg, e);

        }
    }

    @Deactivate
    protected void deactivate(ComponentContext ctxt) {


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
