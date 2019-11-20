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

package org.wso2.carbon.identity.application.authenticator.biometric;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.AbstractApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.FederatedApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authenticator.biometric.dao.impl.BiometricDAOImpl;
import org.wso2.carbon.identity.application.authenticator.biometric.notification.handler.impl.PushNotificationSenderImpl;
import org.wso2.carbon.identity.application.common.model.Property;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Biometric Authenticator class.
 */
public class BiometricAuthenticator extends AbstractApplicationAuthenticator
        implements FederatedApplicationAuthenticator {

    private static final Log log = LogFactory.getLog(BiometricAuthenticator.class);
    private static String sessionDataKey;
    private static String randomChallenge;
    private static String signedChallenge;

    /**
     * Get the friendly name of the Authenticator.
     */
    @Override
    public String getFriendlyName() {

        return BiometricAuthenticatorConstants.AUTHENTICATOR_FRIENDLY_NAME;
    }

    @Override
    public boolean canHandle(HttpServletRequest request) {

        signedChallenge = request.getParameter(BiometricAuthenticatorConstants.SIGNED_CHALLENGE);
        return signedChallenge != null;
    }

    @Override
    public String getContextIdentifier(javax.servlet.http.HttpServletRequest request) {

        sessionDataKey = request.getParameter(BiometricAuthenticatorConstants.SESSION_DATA_KEY);
        return sessionDataKey;
    }

    /**
     * Get the name of the Authenticator.
     */
    @Override
    public String getName() {

        return BiometricAuthenticatorConstants.AUTHENTICATOR_NAME;
    }

    @Override
    protected void initiateAuthenticationRequest(HttpServletRequest request, HttpServletResponse response,
                                                 AuthenticationContext context) {

        AuthenticatedUser user = context.getSequenceConfig().getStepMap().get(1).getAuthenticatedUser();
        String username = user.getUserName();

        Map<String, String> authenticatorProperties = context.getAuthenticatorProperties();
        String serverKey = authenticatorProperties.get(BiometricAuthenticatorConstants.SERVER_KEY);

        String hostname = IdentityUtil.getHostName();
        String serviceProviderName = context.getServiceProviderName();
        String message = username + " is trying to log into " + serviceProviderName + " from " + hostname;

        UUID challenge = UUID.randomUUID();
        randomChallenge = challenge.toString();

        BiometricDAOImpl biometricDAO = BiometricDAOImpl.getInstance();
        String deviceID = biometricDAO.getDeviceID(username);

        PushNotificationSenderImpl pushNotificationSender = PushNotificationSenderImpl.getInstance();
        pushNotificationSender.sendPushNotification(deviceID, serverKey, message, randomChallenge, sessionDataKey);

        try {
            String pollingEndpoint = "https://biometricauthenticator.private.wso2.com:9443/" +
                    "authenticationendpoint/wait.jsp?sessionDataKey=";
            String waitPage = pollingEndpoint + sessionDataKey;
            response.sendRedirect(waitPage);
        } catch (IOException e) {
            log.error("Error when trying to redirect to wait.jsp page");
        }
    }

    /**
     * Process the response of the Biometric end-point.
     *
     * @param httpServletRequest    the HttpServletRequest
     * @param httpServletResponse   the HttpServletResponse
     * @param authenticationContext the AuthenticationContext
     */
    @Override
    protected void processAuthenticationResponse(HttpServletRequest httpServletRequest, HttpServletResponse
            httpServletResponse, AuthenticationContext authenticationContext) {

        if (randomChallenge.equals(signedChallenge)) {
            AuthenticatedUser user = authenticationContext.getSequenceConfig().
                    getStepMap().get(1).getAuthenticatedUser();
            authenticationContext.setSubject(user);
        } else {
            log.error("Sent and received challenges are not the same!");
        }
    }

    /**
     * Get Configuration Properties.
     */
    @Override
    public List<Property> getConfigurationProperties() {

        String firebaseServerKey = "Firebase Server Key";
        List<Property> configProperties = new ArrayList<>();
        Property serverKey = new Property();
        serverKey.setName(BiometricAuthenticatorConstants.SERVER_KEY);
        serverKey.setDisplayName(firebaseServerKey);
        serverKey.setDescription("Enter the firebase server key of the android app");
        serverKey.setDisplayOrder(1);
        configProperties.add(serverKey);
        return configProperties;
    }
}
