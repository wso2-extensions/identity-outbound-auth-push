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

package org.wso2.carbon.identity.application.authenticator.biometric.endpoint.servlet;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authenticator.biometric.endpoint.model.WaitStatus;
import org.wso2.carbon.identity.application.authenticator.biometric.endpoint.javascript.flow.WaitStatusResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Component class for implementing the Biometric endpoint.
 */
public class BiometricServlet extends HttpServlet {
    private static final Log log = LogFactory.getLog(BiometricServlet.class);
    private static final String T = "t";
    private static final String MOBILE = "mobile";
    private static final String WEB = "web";
    private static final String SDK_MOBILE = "SDKMobile";
    private static final String SDK_WEB = "SDKWeb";
    private static final String CHALLENGE_MOBILE = "CHALLENGEMobile";
    private HashMap<String, String> updateStatus = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        WaitStatusResponse waitResponse = null;
        if (request.getParameterMap().containsKey(T)) {
            String parameterT = request.getParameter(T);

            waitResponse = new WaitStatusResponse();

            if (parameterT.equals(MOBILE)) {
                if ((request.getParameterMap().containsKey(SDK_MOBILE)) &&
                        request.getParameterMap().containsKey(CHALLENGE_MOBILE)) {
                    String sdkMobile = request.getParameter(SDK_MOBILE);
                    String challengeMobile = request.getParameter(CHALLENGE_MOBILE);
//                    log.info("challenge mobile parameter : " + challengeMobile);
//                    log.info("sdk mobile parameter : " + sdkmobile);
//                    log.info("consent mobile  is : " + consentMobile);
                    updateStatus.put(sdkMobile, challengeMobile);
//                    log.info("table is: " + updateStatus);
                    response.setContentType("text/html");
                    response.setStatus(200);
                    PrintWriter out = response.getWriter();
                    if (log.isDebugEnabled()) {
                        log.debug("<h3>received the mobile SDK and challenge !</h3>");
                    }
                }

            } else if (parameterT.equals(WEB)) {
                if (request.getParameterMap().containsKey(SDK_WEB)) {
                    String sdkweb = request.getParameter(SDK_WEB);
                    String signedChallengeExtracted = updateStatus.get(sdkweb);
                    if (signedChallengeExtracted != null && updateStatus.containsKey(sdkweb)) {
                        response.setContentType("text/html");
                        response.setCharacterEncoding("utf-8");
                        response.setStatus(200);
                        request.setAttribute("signedChallenge", signedChallengeExtracted);

                        waitResponse.setStatus(WaitStatus.Status.COMPLETED1.name());
                        waitResponse.setChallenge(signedChallengeExtracted);
                        updateStatus.remove(sdkweb);
                        if (log.isDebugEnabled()) {
                            log.debug("a response from the mobile device!!!");
                        }

                    } else {
                        response.setContentType("text/html");
                        response.setStatus(401);
                        response.sendError(401, "a 401 error occurs ");
                    }
                }
            }
        } else {
            response.setContentType("text/html");
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
