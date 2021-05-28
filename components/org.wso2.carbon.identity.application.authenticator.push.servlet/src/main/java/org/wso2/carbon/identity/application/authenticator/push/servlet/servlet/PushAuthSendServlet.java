/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authenticator.push.exception.PushAuthenticatorException;
import org.wso2.carbon.identity.application.authenticator.push.notification.handler.RequestSender;
import org.wso2.carbon.identity.application.authenticator.push.notification.handler.impl.RequestSenderImpl;
import org.wso2.carbon.identity.application.authenticator.push.servlet.PushServletConstants;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for sending an auth request from the select device page.
 */
public class PushAuthSendServlet extends HttpServlet {

    private static final Log log = LogFactory.getLog(PushAuthSendServlet.class);
    private static final long serialVersionUID = 3499692110941777290L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String key = request.getParameter(PushServletConstants.SESSION_DATA_KEY);

        String deviceId = request.getParameter(PushServletConstants.DEVICE_ID);
        RequestSender requestSender = new RequestSenderImpl();
        try {
            requestSender.sendRequest(request, response, deviceId, key);
        } catch (PushAuthenticatorException e) {
            String errorMessage = String.format(PushServletConstants
                    .ErrorMessages.ERROR_CODE_SEND_REQUEST_FAILED.toString(), deviceId);
            throw new ServletException(errorMessage, e);
        }
    }
}
