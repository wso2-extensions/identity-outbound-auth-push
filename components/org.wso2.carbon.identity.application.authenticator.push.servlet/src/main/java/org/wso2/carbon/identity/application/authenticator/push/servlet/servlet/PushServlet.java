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
import org.wso2.carbon.identity.application.authenticator.push.PushAuthenticatorConstants;
import org.wso2.carbon.identity.application.authenticator.push.cache.AuthContextCache;
import org.wso2.carbon.identity.application.authenticator.push.cache.AuthContextCacheEntry;
import org.wso2.carbon.identity.application.authenticator.push.cache.AuthContextcacheKey;
import org.wso2.carbon.identity.application.authenticator.push.dto.AuthDataDTO;
import org.wso2.carbon.identity.application.authenticator.push.exception.PushAuthenticatorException;
import org.wso2.carbon.identity.application.authenticator.push.notification.handler.RequestSender;
import org.wso2.carbon.identity.application.authenticator.push.notification.handler.impl.RequestSenderImpl;
import org.wso2.carbon.identity.application.authenticator.push.validator.PushJWTValidator;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandler;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.impl.DeviceHandlerImpl;
import org.wso2.carbon.identity.application.authenticator.push.servlet.store.impl.PushDataStoreImpl;

import java.io.IOException;
import java.sql.SQLException;
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
    private PushDataStoreImpl pushDataStoreInstance = PushDataStoreImpl.getInstance();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        // Handles request from devices page for sending the request from the selected device

        String key = request.getParameter("sessionDataKey");

        String deviceId = request.getParameter("deviceId");
        RequestSender requestSender = new RequestSenderImpl();
        try {
            requestSender.sendRequest(request, response, deviceId, key);
        } catch (PushAuthenticatorException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Handles request from the mobile app for authentication

        String action = request.getParameter("ACTION");
        if (action == null) {
            action = "AUTH_REQUEST";
        }
        if (action.equals("DELETE")) {
            try {
                deleteDevice(request, response);
            } catch (PushDeviceHandlerClientException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Device not found");
            } catch (PushDeviceHandlerServerException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Server Error");
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "SQL Exception");
            }
        } else {
            handleMobileResponse(request, response);
        }

    }

    private void handleMobileResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonObject json = new JsonParser().parse(request.getReader().readLine()).getAsJsonObject();
        String token = json.get("authResponse").getAsString();
        if (StringUtils.isEmpty(token)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Received authentication response token is null");
        } else {
            // If the query parameters session data key and challenge are not null, else block is executed..
            PushJWTValidator validator = new PushJWTValidator();
            String sessionDataKey = validator.getSessionDataKey(token);

            if (StringUtils.isEmpty(sessionDataKey)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Received authentication response doesn't contain required session data key");
            }

            AuthenticationContext context = AuthContextCache.getInstance().getValueFromCacheByRequestId
                    (new AuthContextcacheKey(sessionDataKey)).getAuthenticationContext();

            AuthDataDTO authDataDTO = (AuthDataDTO) context.getProperty("authData");
            authDataDTO.setAuthToken(token);
            context.setProperty("authData", authDataDTO);
            AuthContextCache.getInstance().addToCacheByRequestId(new AuthContextcacheKey(sessionDataKey),
                    new AuthContextCacheEntry(context));

            String sessionDataKeyMobile = validator.getSessionDataKey(token);
            String status = PushAuthenticatorConstants.COMPLETED;
            pushDataStoreInstance.addPushData(sessionDataKeyMobile, status);
            response.setStatus(HttpServletResponse.SC_OK);
            if (log.isDebugEnabled()) {
                log.debug("Session data key received from the mobile application: " + sessionDataKeyMobile);
            }
        }
    }

    private void deleteDevice(HttpServletRequest request, HttpServletResponse response)
            throws PushDeviceHandlerClientException, PushDeviceHandlerServerException, SQLException {

        DeviceHandler deviceHandler = new DeviceHandlerImpl();
        deviceHandler.unregisterDevice(request.getParameter("deviceId"));
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
