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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authenticator.biometric.servlet.javascript.flow.WaitStatusResponse;
import org.wso2.carbon.identity.application.authenticator.biometric.servlet.model.WaitStatus;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Component class for implementing the Biometric servlet.
 */
public class BiometricServlet extends HttpServlet {
    private static final Log log = LogFactory.getLog(BiometricServlet.class);
    private static final String DEVICE_TYPE = "deviceType";
    private static final String MOBILE = "mobile";
    private static final String WEB = "web";
    private static final String SESSION_DATA_KEY_MOBILE = "sessionDataKeyMobile";
    private static final String SESSION_DATA_KEY_WEB = "sessionDataKeyWeb";
    private static final String CHALLENGE_MOBILE = "challengeMobile";
    private static final String TEXT_HTML = "text/html";
    private HashMap<String, String> updateStatus = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        WaitStatusResponse waitResponse = new WaitStatusResponse();;
        if (request.getParameterMap().containsKey(DEVICE_TYPE)) {
            String deviceType = request.getParameter(DEVICE_TYPE);
            if (deviceType.equals(MOBILE)) {
                if (request.getParameterMap().containsKey(SESSION_DATA_KEY_MOBILE) &&
                        request.getParameterMap().containsKey(CHALLENGE_MOBILE)) {
                    String sessionDataKeyMobile = request.getParameter(SESSION_DATA_KEY_MOBILE);
                    String challengeMobile = request.getParameter(CHALLENGE_MOBILE);
                    updateStatus.put(sessionDataKeyMobile, challengeMobile);
                    response.setContentType(TEXT_HTML);
                    response.setStatus(200);
                    if (log.isDebugEnabled()) {
                        log.debug("received the mobile session data key and challenge !");
                    }
                }
            } else if (deviceType.equals(WEB)) {
                if (request.getParameterMap().containsKey(SESSION_DATA_KEY_WEB)) {
                    String sessionDataKeyWeb = request.getParameter(SESSION_DATA_KEY_WEB);
                    String signedChallengeExtracted = updateStatus.get(sessionDataKeyWeb);
                    if (signedChallengeExtracted != null) {
                        response.setContentType(TEXT_HTML);
                        response.setCharacterEncoding("utf-8");
                        response.setStatus(200);
                        request.setAttribute("signedChallenge", signedChallengeExtracted);

                        waitResponse.setStatus(WaitStatus.Status.COMPLETED.name());
                        waitResponse.setChallenge(signedChallengeExtracted);
                        updateStatus.remove(sessionDataKeyWeb);

                    } else {
                        response.setContentType(TEXT_HTML);
                        response.setStatus(401);
                        response.sendError(401, "a 401 error occurs ");
                    }
                }
            }
        } else {
            response.setContentType(TEXT_HTML);
            response.setStatus(400);
            PrintWriter out = response.getWriter();
            out.println("<h3>Invalid request... !</h3>");
        }

        response.setContentType("application/json");
        String json = new Gson().toJson(waitResponse);
        try (PrintWriter out = response.getWriter()) {
            if (log.isDebugEnabled()) {
                log.debug("json waitResponse: " + json);
            }
            out.print(json);
            out.flush();
        }
    }
}
