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

package org.wso2.carbon.identity.application.authenticator.push;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.wso2.carbon.identity.application.authentication.framework.AbstractApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.FederatedApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.inbound.InboundConstants;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authenticator.push.common.PushAuthContextManager;
import org.wso2.carbon.identity.application.authenticator.push.common.PushJWTValidator;
import org.wso2.carbon.identity.application.authenticator.push.common.exception.PushAuthTokenValidationException;
import org.wso2.carbon.identity.application.authenticator.push.common.impl.PushAuthContextManagerImpl;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandler;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.impl.DeviceHandlerImpl;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.Device;
import org.wso2.carbon.identity.application.authenticator.push.dto.AuthDataDTO;
import org.wso2.carbon.identity.application.authenticator.push.exception.PushAuthenticatorException;
import org.wso2.carbon.identity.application.authenticator.push.internal.PushAuthenticatorServiceComponent;
import org.wso2.carbon.identity.application.authenticator.push.notification.handler.RequestSender;
import org.wso2.carbon.identity.application.authenticator.push.notification.handler.impl.RequestSenderImpl;
import org.wso2.carbon.identity.application.authenticator.push.util.Config;
import org.wso2.carbon.identity.application.common.model.Property;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is the class that implements the push authenticator feature.
 */
public class PushAuthenticator extends AbstractApplicationAuthenticator
        implements FederatedApplicationAuthenticator {

    private static final long serialVersionUID = 8272421416671799253L;
    private static final Log log = LogFactory.getLog(PushAuthenticator.class);

    @Override
    public String getFriendlyName() {

        return PushAuthenticatorConstants.AUTHENTICATOR_FRIENDLY_NAME;
    }

    @Override
    public boolean canHandle(HttpServletRequest request) {

        return request.getParameter(PushAuthenticatorConstants.PROCEED_AUTH) != null;
    }

    @Override
    public String getContextIdentifier(javax.servlet.http.HttpServletRequest request) {

        return request.getParameter(InboundConstants.RequestProcessor.CONTEXT_KEY);
    }

    @Override
    public String getName() {

        return PushAuthenticatorConstants.AUTHENTICATOR_NAME;
    }

    @Override
    protected void initiateAuthenticationRequest(HttpServletRequest request, HttpServletResponse response,
                                                 AuthenticationContext context) throws AuthenticationFailedException {

        DeviceHandler deviceHandler = new DeviceHandlerImpl();
        PushAuthContextManager contextManager = new PushAuthContextManagerImpl();
        AuthenticatedUser user = context.getSequenceConfig().getStepMap().
                get(context.getCurrentStep() - 1).getAuthenticatedUser();
        String sessionDataKey = request.getParameter(InboundConstants.RequestProcessor.CONTEXT_KEY);
        try {
            List<Device> deviceList;
            deviceList = deviceHandler.listDevices(getUserIdFromUsername(user.getUserName(), getUserRealm(user)));
            request.getSession().setAttribute(PushAuthenticatorConstants.DEVICES_LIST, deviceList);
            JSONObject object;
            JSONArray array = new JSONArray();

            for (Device device : deviceList) {
                object = new JSONObject();
                object.put(PushAuthenticatorConstants.DEVICE_ID, device.getDeviceId());
                object.put(PushAuthenticatorConstants.DEVICE_NAME, device.getDeviceName());
                object.put(PushAuthenticatorConstants.DEVICE_MODEL, device.getDeviceModel());
                object.put(PushAuthenticatorConstants.LAST_TIME_USED, device.getLastUsedTime().toString());
                array.add(object);
            }

            AuthDataDTO authDataDTO = new AuthDataDTO();
            context.setProperty(PushAuthenticatorConstants.CONTEXT_AUTH_DATA, authDataDTO);
            contextManager.storeContext(sessionDataKey, context);

            if (deviceList.size() == 1) {
                RequestSender requestSender = new RequestSenderImpl();
                requestSender.sendRequest(request, response, deviceList.get(0).getDeviceId(), sessionDataKey);
                redirectWaitPage(response, sessionDataKey, user);
            } else if (deviceList.size() == 0) {
                if (log.isDebugEnabled()) {
                    log.debug("User (" + user.toFullQualifiedUsername() + ") does not have any registered devices "
                            + "for push based authentication.");
                }

                redirectRetryPage(response, PushAuthenticatorConstants.NO_REGISTERED_DEVICES_PARAM,
                        PushAuthenticatorConstants.NO_REGISTERED_DEVICES_MESSAGE);
            } else {
                String errorMessage = String.format("Error occurred as user (%s) has more than one device registered "
                        + "for push based authentication.", user.toFullQualifiedUsername());
                log.error(errorMessage);
                redirectRetryPage(response, PushAuthenticatorConstants.DEVICES_OVER_LIMIT_PARAM,
                        PushAuthenticatorConstants.DEVICES_OVER_LIMIT_MESSAGE);
            }

        } catch (PushDeviceHandlerServerException e) {
            throw new AuthenticationFailedException("Error occurred when trying to redirect to the registered devices"
                    + " page. Devices were not found for user: " + user.toFullQualifiedUsername() + ".", e);
        } catch (IOException e) {
            throw new AuthenticationFailedException("Error occurred when trying to redirect to the registered devices"
                    + " page for user: " + user.toFullQualifiedUsername() + ".", e);
        } catch (PushAuthenticatorException e) {
            throw new AuthenticationFailedException("Error occurred when trying to get user claims for user: "
                    + user.toFullQualifiedUsername() + ".", e);
        } catch (UserStoreException e) {
            throw new AuthenticationFailedException("Error occurred when trying to get the user ID for user: "
                    + user.toFullQualifiedUsername() + ".", e);
        }

    }

    @Override
    protected void processAuthenticationResponse(HttpServletRequest httpServletRequest, HttpServletResponse
            httpServletResponse, AuthenticationContext authenticationContext) throws AuthenticationFailedException {

        AuthenticatedUser user = authenticationContext.getSequenceConfig().
                getStepMap().get(authenticationContext.getCurrentStep() - 1).getAuthenticatedUser();

        PushAuthContextManager contextManager = new PushAuthContextManagerImpl();
        AuthenticationContext sessionContext = contextManager.getContext(httpServletRequest
                .getParameter(PushAuthenticatorConstants.SESSION_DATA_KEY));
        AuthDataDTO authDataDTO = (AuthDataDTO) sessionContext
                .getProperty(PushAuthenticatorConstants.CONTEXT_AUTH_DATA);

        String authResponseToken = authDataDTO.getAuthToken();
        String serverChallenge = authDataDTO.getChallenge();

        String deviceId = getDeviceIdFromToken(authResponseToken);
        String publicKey = getPublicKey(deviceId);

        PushJWTValidator validator = new PushJWTValidator();
        JWTClaimsSet claimsSet;
        try {
            claimsSet = validator.getValidatedClaimSet(authResponseToken, publicKey);
        } catch (PushAuthTokenValidationException e) {
            String errorMessage = String
                    .format("Error occurred when trying to validate the JWT signature from device: %s of user: %s.",
                            deviceId, user);
            throw new AuthenticationFailedException(errorMessage, e);
        }
        try {
            if (claimsSet != null) {
                validateChallenge(claimsSet, serverChallenge, deviceId);

                String authStatus = claimsSet.getStringClaim("res");
                if (authStatus.equals(PushAuthenticatorConstants.AUTH_REQUEST_STATUS_SUCCESS)) {
                    authenticationContext.setSubject(user);
                } else if (authStatus.equals(PushAuthenticatorConstants.AUTH_REQUEST_STATUS_DENIED)) {
                    redirectRetryPage(httpServletResponse, PushAuthenticatorConstants.AUTH_DENIED_PARAM,
                            PushAuthenticatorConstants.AUTH_DENIED_MESSAGE);
                } else {
                    String errorMessage = String.format("Authentication failed! Auth status for user" +
                            " '%s' is not available in JWT.", user.toFullQualifiedUsername());
                    throw new AuthenticationFailedException(errorMessage);
                }
            } else {
                String errorMessage = String
                        .format("Authentication failed! JWT signature is not valid for device: %s of user: %s.",
                                deviceId, user.toFullQualifiedUsername());
                throw new AuthenticationFailedException(errorMessage);
            }

        } catch (IOException e) {
            String errorMessage = String.format(
                    "Error occurred when redirecting to the request denied page for device: %s of user: %s.",
                    deviceId, user.toFullQualifiedUsername());
            throw new AuthenticationFailedException(errorMessage, e);
        } catch (ParseException e) {
            throw new AuthenticationFailedException("Error occurred when parsing the authentication response token", e);
        }

        try {
            contextManager.clearContext(claimsSet.getStringClaim(PushAuthenticatorConstants.TOKEN_SESSION_DATA_KEY));
        } catch (ParseException e) {
            throw new AuthenticationFailedException("Error occurred when getting token claim to clear cache", e);
        }
    }

    @Override
    public List<Property> getConfigurationProperties() {

        List<Property> configProperties = new ArrayList<>();

        String firebaseServerKey = "Firebase Server Key";
        Property serverKeyProperty = new Property();
        serverKeyProperty.setName(PushAuthenticatorConstants.SERVER_KEY);
        serverKeyProperty.setDisplayName(firebaseServerKey);
        serverKeyProperty.setDescription("Enter the firebase server key ");
        serverKeyProperty.setDisplayOrder(0);
        serverKeyProperty.setRequired(true);
        serverKeyProperty.setConfidential(true);
        configProperties.add(serverKeyProperty);

        String fcmUrl = "Firebase url";
        Property fcmUrlProperty = new Property();
        fcmUrlProperty.setName(PushAuthenticatorConstants.FCM_URL);
        fcmUrlProperty.setDisplayName(fcmUrl);
        fcmUrlProperty.setDescription("Enter the url of firebase endpoint ");
        fcmUrlProperty.setDisplayOrder(1);
        fcmUrlProperty.setConfidential(false);
        configProperties.add(fcmUrlProperty);
        return configProperties;
    }

    /**
     * Validate the correlation between the challenged saved in the server and received from the auth response.
     *
     * @param claimsSet JWT claim set for the validated auth response token
     * @param challenge Challenge stored in cache to correlate with JWT
     * @param deviceId  Unique ID for the device trying to authenticate the user
     * @throws AuthenticationFailedException if the jwt fails to parse when getting the challenge claim
     */
    private void validateChallenge(JWTClaimsSet claimsSet, String challenge, String deviceId)
            throws AuthenticationFailedException {

        try {
            if (claimsSet != null) {
                if (!claimsSet.getStringClaim(PushAuthenticatorConstants.TOKEN_CHALLENGE).equals(challenge)) {
                    String errorMessage = String
                            .format("Authentication failed! Challenge received from %s  was not valid.", deviceId);
                    throw new AuthenticationFailedException(errorMessage);
                }
            } else {
                String errorMessage = String
                        .format("Authentication failed! JWT claim set received from %s  was null.", deviceId);
                throw new AuthenticationFailedException(errorMessage);
            }
        } catch (ParseException e) {
            throw new AuthenticationFailedException("Failed to get challenge from the auth response token received " +
                    "from device: " + deviceId + ".");
        }
    }

    /**
     * Get the user realm for the authenticated user.
     *
     * @param authenticatedUser Authenticated user
     * @return User realm
     * @throws AuthenticationFailedException if an error occurs when getting the user realm
     */
    private UserRealm getUserRealm(AuthenticatedUser authenticatedUser) throws AuthenticationFailedException {

        UserRealm userRealm = null;
        try {
            if (authenticatedUser != null) {
                String tenantDomain = authenticatedUser.getTenantDomain();
                int tenantId = IdentityTenantUtil.getTenantId(tenantDomain);
                RealmService realmService = PushAuthenticatorServiceComponent.getRealmService();
                userRealm = realmService.getTenantUserRealm(tenantId);
            }
        } catch (UserStoreException e) {
            throw new AuthenticationFailedException("Error occurred when trying to get the user realm for user: "
                    + authenticatedUser.toFullQualifiedUsername() + ".", e);
        }
        return userRealm;
    }

    /**
     * Get the user ID from the username.
     *
     * @param username username of the user
     * @param realm    user realm for the tenant
     * @return user ID
     * @throws UserStoreException userstore exception
     */
    private String getUserIdFromUsername(String username, UserRealm realm) throws UserStoreException {

        AbstractUserStoreManager userStoreManager = (AbstractUserStoreManager) realm.getUserStoreManager();
        return userStoreManager.getUserIDFromUserName(username);
    }

    /**
     * Derive the Device ID from the auth response token header.
     *
     * @param token Auth response token
     * @return Device ID
     * @throws AuthenticationFailedException if the token string fails to parse to JWT
     */
    private String getDeviceIdFromToken(String token) throws AuthenticationFailedException {

        try {
            return String.valueOf(JWTParser.parse(token).getHeader()
                    .getCustomParam(PushAuthenticatorConstants.TOKEN_DEVICE_ID));
        } catch (ParseException e) {
            throw new AuthenticationFailedException("Error occurred when trying to get the device ID", e);
        }
    }

    /**
     * Get the public key for the device by the device ID.
     *
     * @param deviceId Unique ID for the device
     * @return Public key string
     * @throws AuthenticationFailedException if an error occurs while getting the public key
     */
    private String getPublicKey(String deviceId) throws AuthenticationFailedException {

        DeviceHandler deviceHandler = new DeviceHandlerImpl();
        try {
            return deviceHandler.getPublicKey(deviceId);
        } catch (PushDeviceHandlerServerException e) {
            throw new AuthenticationFailedException("Error occurred when trying to get the public key.");
        } catch (PushDeviceHandlerClientException e) {
            throw new AuthenticationFailedException("Public key error");
        }
    }

    /**
     * Redirect user to device selection page.
     *
     * @param response          HTTP response
     * @param context           Authentication context
     * @param sessionDataKey    Unique key for the session
     * @param deviceArrayString JSON array as a string
     * @throws IOException if an error occurs when redirecting to the device selection page
     */
    private void redirectDevicesPage(HttpServletResponse response, AuthenticationContext context,
                                     String sessionDataKey, String deviceArrayString) throws IOException {

        Config config = new Config();
        String devicesPage;
        devicesPage = config.getDevicesPage(context)
                + "?sessionDataKey=" + URLEncoder.encode(sessionDataKey, StandardCharsets.UTF_8.name())
                + "&devices=" + URLEncoder.encode(deviceArrayString, StandardCharsets.UTF_8.name());
        response.sendRedirect(devicesPage);
    }

    /**
     * Redirect the user to the wait page.
     *
     * @param response       HTTP response
     * @param sessionDataKey Unique ID for the session
     * @param user           Authenticated user
     * @throws PushAuthenticatorException if an error occurs while trying to redirect to the wait page
     */
    private void redirectWaitPage(HttpServletResponse response, String sessionDataKey, AuthenticatedUser user)
            throws PushAuthenticatorException {

        try {
            String waitPage = PushAuthenticatorConstants.WAIT_PAGE
                    + "?sessionDataKey="
                    + URLEncoder.encode(sessionDataKey, StandardCharsets.UTF_8.name());
            response.sendRedirect(waitPage);
        } catch (IOException e) {
            String errorMessage = String.format("Error occurred when trying to to redirect user: %s to the wait page.",
                    user.toFullQualifiedUsername());
            throw new PushAuthenticatorException(errorMessage, e);
        }
    }

    /**
     * Redirect the user to the authentication denied page.
     *
     * @param httpServletResponse HTTP response
     * @throws IOException if an error occurs while redirecting to the denied page
     */
    private void redirectRetryPage(HttpServletResponse httpServletResponse, String status, String message)
            throws IOException {

        String deniedPage = "/authenticationendpoint/retry.do"
                + "?status=" + URLEncoder.encode(status, StandardCharsets.UTF_8.name())
                + "&statusMsg=" + URLEncoder.encode(message, StandardCharsets.UTF_8.name());
        httpServletResponse.sendRedirect(deniedPage);
    }

}
