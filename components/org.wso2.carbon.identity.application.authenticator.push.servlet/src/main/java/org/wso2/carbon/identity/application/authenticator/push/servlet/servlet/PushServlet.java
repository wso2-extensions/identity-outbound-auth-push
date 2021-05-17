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

package org.wso2.carbon.identity.application.authenticator.push.servlet.servlet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authenticator.push.core.PushAuthContextManager;
import org.wso2.carbon.identity.application.authenticator.push.core.PushJWTValidator;
import org.wso2.carbon.identity.application.authenticator.push.core.impl.PushAuthContextManagerImpl;
import org.wso2.carbon.identity.application.authenticator.push.dto.AuthDataDTO;
import org.wso2.carbon.identity.application.authenticator.push.servlet.PushServletConstants;
import org.wso2.carbon.identity.application.authenticator.push.servlet.store.impl.PushDataStoreImpl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The Push Servlet class manages the status of the push authentication process with the session data key,
 * by processing the responses from the push authenticator devices(mobile devices) and replying to the polling
 * requests from the web client.
 */
public class PushServlet extends HttpServlet {

    private static final long serialVersionUID = -2050679246736808648L;
    private static final Log log = LogFactory.getLog(PushServlet.class);
    private final PushDataStoreImpl pushDataStoreInstance = PushDataStoreImpl.getInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        if (log.isDebugEnabled()) {
            log.error("Authentication request received from mobile app.");
        }

        handleMobileResponse(request, response);
    }

    /**
     * Handles authentication request received from mobile app
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @throws IOException
     */
    private void handleMobileResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PushAuthContextManager contextManager = new PushAuthContextManagerImpl();
        JsonObject json = new JsonParser().parse(request.getReader().readLine()).getAsJsonObject();
        String token = json.get(PushServletConstants.AUTH_RESPONSE).getAsString();
        if (StringUtils.isEmpty(token)) {
            String errorMessage = "The request did not have an authentication response token.";
            if (log.isDebugEnabled()) {
                log.error(errorMessage);
            }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
        } else {
            PushJWTValidator validator = new PushJWTValidator();
            String sessionDataKey = validator.getSessionDataKey(token);

            if (StringUtils.isEmpty(sessionDataKey)) {
                String errorMessage = String.format("Authentication response token received from device: %s doesn't "
                        + "contain a session data key.", validator.getDeviceId(token));
                if (log.isDebugEnabled()) {
                    log.error(errorMessage);
                }
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Authentication response token doesn't have "
                        + "a session data key.");
                return;
            }

            AuthenticationContext context = contextManager.getContext(sessionDataKey);

            AuthDataDTO authDataDTO = (AuthDataDTO) context.getProperty(PushServletConstants.AUTH_DATA);
            authDataDTO.setAuthToken(token);
            context.setProperty(PushServletConstants.AUTH_DATA, authDataDTO);
            contextManager.storeContext(sessionDataKey, context);

            String sessionDataKeyMobile = validator.getSessionDataKey(token);
            String status = PushServletConstants.Status.COMPLETED.name();
            pushDataStoreInstance.addPushData(sessionDataKeyMobile, status);
            response.setStatus(HttpServletResponse.SC_OK);
            if (log.isDebugEnabled()) {
                log.debug("Completed processing auth response from mobile app.");
            }
        }
    }

}
