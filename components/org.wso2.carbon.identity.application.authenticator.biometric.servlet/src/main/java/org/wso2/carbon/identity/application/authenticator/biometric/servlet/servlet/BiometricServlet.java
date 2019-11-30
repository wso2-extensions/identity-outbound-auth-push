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

package org.wso2.carbon.identity.application.authenticator.biometric.servlet.servlet;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authenticator.biometric.servlet.BiometricServletConstants;
import org.wso2.carbon.identity.application.authenticator.biometric.servlet.model.WaitStatus;
import org.wso2.carbon.identity.application.authenticator.biometric.servlet.store.impl.BiometricDataStoreImpl;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Component class for implementing the Biometric servlet.
 * GET calls(with query parameter:session data key) from the client web side and POST calls(with query parameters
 * :session data key and signed challenge) from the client android app are handled here by
 * updating the hashmap "updateStatus".
 *
 */
public class BiometricServlet extends HttpServlet {

    private static final long serialVersionUID = -2050679246736808648L;
    private static final Log log = LogFactory.getLog(BiometricServlet.class);
    private BiometricDataStoreImpl biometricDataStoreInstance = BiometricDataStoreImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (!(request.getParameterMap().containsKey(BiometricServletConstants.INITIATOR))) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid syntax as the query parameter for initiator is missing.");
            }
        } else { // If the initiator is not null, else block is executed.
            String initiator = request.getParameter(BiometricServletConstants.INITIATOR);
            if (!(BiometricServletConstants.WEB.equals(initiator) && request.getParameterMap()
                    .containsKey(BiometricServletConstants.CONTEXT_KEY))) {
                if (log.isDebugEnabled()) {
                    log.debug("Unsupported HTTP GET request or session data key is null.");
                }
            } else { // If the initiator is equal to WEB and if the query parameter session data
                // key is not null, else block is executed.
                handleWebResponse(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (!request.getParameterMap().containsKey(BiometricServletConstants.INITIATOR)) {
            PrintWriter out = response.getWriter();
            out.println("Invalid request!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/html");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Query parameter for initiator is missing.");
            response.getWriter().write("Invalid syntax as the query parameter for initiator is missing.");
            response.getWriter().flush();
            return;
        }
        String initiator = request.getParameter(BiometricServletConstants.INITIATOR);

        if (!BiometricServletConstants.MOBILE.equals(initiator)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported HTTP request from a mobile device.");
            return;
        }
        handleMobileResponse(request, response);
    }

    private void handleWebResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WaitStatus waitResponse = new WaitStatus();
        String sessionDataKeyWeb = request.getParameter(BiometricServletConstants.CONTEXT_KEY);
        String signedChallengeExtracted = biometricDataStoreInstance.getSignedChallenge(sessionDataKeyWeb);
        if (StringUtils.isEmpty(signedChallengeExtracted)) {
            if (log.isDebugEnabled()) {
                log.debug("Signed challenge sent from the mobile application is null.");
            }

        } else {  // If the signed challenge sent from the mobile application is not null,else block is executed..
            response.setStatus(HttpServletResponse.SC_OK);
            request.setAttribute(BiometricServletConstants.SIGNED_CHALLENGE, signedChallengeExtracted);
            waitResponse.setStatus(BiometricServletConstants.Status.COMPLETED.name());
            waitResponse.setChallenge(signedChallengeExtracted);
            biometricDataStoreInstance.removeBiometricData(sessionDataKeyWeb);
            response.setContentType(BiometricServletConstants.APPLICATION_JSON);
            String json = new Gson().toJson(waitResponse);
            if (log.isDebugEnabled()) {
                log.debug("Json Response to the wait page: " + json);
            }
            try (PrintWriter out = response.getWriter()) {
                out.print(json);
                out.flush();
            }
        }
    }

    private void handleMobileResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!(request.getParameterMap().containsKey(BiometricServletConstants.CONTEXT_KEY) &&
                request.getParameterMap().containsKey(BiometricServletConstants.CHALLENGE))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST , "Received session data key and/or signed" +
                    " challenge is null.");

        } else { // If the query parameters session data key and challenge are not null, else block is executed..
            String sessionDataKeyMobile = request.getParameter(BiometricServletConstants.CONTEXT_KEY);
            String challengeMobile = request.getParameter(BiometricServletConstants.CHALLENGE);
            biometricDataStoreInstance.addBiometricData(sessionDataKeyMobile, challengeMobile);
            response.setStatus(HttpServletResponse.SC_OK);
            if (log.isDebugEnabled()) {
                log.debug("Session data key received from the mobile application: " + sessionDataKeyMobile);
                log.debug("Signed challenge received from the mobile application: " + challengeMobile);
            }
        }
    }
}
