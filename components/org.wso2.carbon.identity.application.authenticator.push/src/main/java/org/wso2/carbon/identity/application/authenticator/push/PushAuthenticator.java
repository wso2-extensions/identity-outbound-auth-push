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

package org.wso2.carbon.identity.application.authenticator.push;

import org.apache.commons.lang.StringUtils;
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
import org.wso2.carbon.identity.application.authenticator.push.cache.AuthContextCache;
import org.wso2.carbon.identity.application.authenticator.push.cache.AuthContextCacheEntry;
import org.wso2.carbon.identity.application.authenticator.push.cache.AuthContextcacheKey;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandler;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.impl.DeviceHandlerImpl;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.Device;
import org.wso2.carbon.identity.application.authenticator.push.dto.AuthDataDTO;
import org.wso2.carbon.identity.application.authenticator.push.dto.impl.AuthDataDTOImpl;
import org.wso2.carbon.identity.application.authenticator.push.exception.PushAuthenticatorException;
import org.wso2.carbon.identity.application.authenticator.push.internal.PushAuthenticatorServiceComponent;
import org.wso2.carbon.identity.application.authenticator.push.notification.handler.impl.FirebasePushNotificationSenderImpl;
import org.wso2.carbon.identity.application.authenticator.push.validator.PushJWTValidator;
import org.wso2.carbon.identity.application.common.model.Property;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.core.service.RealmService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ua_parser.Parser;
import ua_parser.Client;

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

        return request.getParameter(PushAuthenticatorConstants.SIGNED_CHALLENGE) != null;
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
        AuthenticatedUser user = context.getSequenceConfig().getStepMap().
                get(context.getCurrentStep() - 1).getAuthenticatedUser();
        String sessionDataKey = request.getParameter(InboundConstants.RequestProcessor.CONTEXT_KEY);
        try {
            ArrayList<Device> deviceList = null;
            deviceList = deviceHandler.listDevices(user.getUserName(), user.getUserStoreDomain(),
                    user.getTenantDomain());
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

            AuthDataDTO authDataDTO = new AuthDataDTOImpl();
            context.setProperty(PushAuthenticatorConstants.CONTEXT_AUTH_DATA, authDataDTO);
            AuthContextCache.getInstance().addToCacheByRequestId(new AuthContextcacheKey(sessionDataKey),
                    new AuthContextCacheEntry(context));

            if (deviceList.size() == 1) {
                sendRequest(request, response, deviceList.get(0).getDeviceId(), sessionDataKey);
            } else {

                String string = JSONArray.toJSONString(array);
                String devicesPage = null;
                devicesPage = getDevicesPage(context) + "?sessionDataKey=" + URLEncoder.encode(sessionDataKey,
                        StandardCharsets.UTF_8.name()) + "&devices=" + URLEncoder.encode(string,
                        StandardCharsets.UTF_8.name());
                response.sendRedirect(devicesPage);
            }

        } catch (PushDeviceHandlerServerException e) {
            String errorMessage = "Error occurred when trying to redirect to the registered devices page. "
                    + String.format("Devices were not found for user: %s.", user);
            throw new AuthenticationFailedException(errorMessage, e);
        } catch (PushDeviceHandlerClientException e) {
            String errorMessage = "Error occurred when trying to redirect to registered devices page. "
                    + "Authenticated user was not found.";
            throw new AuthenticationFailedException(errorMessage, e);
        } catch (SQLException e) {
            String errorMessage = String
                    .format("Error occurred when trying to get the device list for user: %s.", user);
            throw new AuthenticationFailedException(errorMessage, e);
        } catch (UserStoreException e) {
            throw new AuthenticationFailedException("Error occurred when trying to get the authenticated user.", e);
        } catch (IOException e) {
            String errorMessage = String
                    .format("Error occurred when trying to redirect to the registered devices page for user: %s.",
                            user);
            throw new AuthenticationFailedException(errorMessage, e);
        } catch (PushAuthenticatorException e) {
            String errorMessage = String.format("Error occurred when trying to get user claims for user: %s.", user);
            throw new AuthenticationFailedException(errorMessage, e);
        }

    }

    /**
     * Get the push-devices.jsp page from authentication.xml file or use the wait page from constant file.
     *
     * @param context the AuthenticationContext
     * @return the waitPage
     * @throws AuthenticationFailedException
     */
    private String getDevicesPage(AuthenticationContext context) throws AuthenticationFailedException {

        String devicesPage = getConfiguredDevicesPage(context);
        if (StringUtils.isEmpty(devicesPage)) {
            devicesPage = PushAuthenticatorConstants.DEVICES_PAGE;
            if (log.isDebugEnabled()) {
                log.debug("Default authentication endpoint context is used.");
            }
        }
        return devicesPage;
    }

    /**
     * Get the wait.jsp page url from the application-authentication.xml file.
     *
     * @param context the AuthenticationContext
     * @return waitPage
     */
    public static String getConfiguredDevicesPage(AuthenticationContext context) {

        return getConfiguration(context, PushAuthenticatorConstants.PUSH_AUTHENTICATION_ENDPOINT_DEVICES_URL);
    }

    /**
     * Get the wait.jsp page from authentication.xml file or use the wait page from constant file.
     *
     * @param context the AuthenticationContext
     * @return the waitPage
     * @throws AuthenticationFailedException
     */
    private String getWaitPage(AuthenticationContext context) throws AuthenticationFailedException {

        String waitPage = getConfiguredWaitPage(context);
        if (StringUtils.isEmpty(waitPage)) {
            waitPage = PushAuthenticatorConstants.WAIT_PAGE;
            if (log.isDebugEnabled()) {
                log.debug("Default authentication endpoint context is used.");
            }
        }
        return waitPage;
    }

    /**
     * Get the wait.jsp page url from the application-authentication.xml file.
     *
     * @param context the AuthenticationContext
     * @return waitPage
     */
    public static String getConfiguredWaitPage(AuthenticationContext context) {

        return getConfiguration(context, PushAuthenticatorConstants.PUSH_AUTHENTICATION_ENDPOINT_WAIT_URL);
    }

    /**
     * Read configurations from application-authentication.xml for given authenticator.
     *
     * @param context    Authentication Context.
     * @param configName Name of the config.
     * @return Config value.
     */
    public static String getConfiguration(AuthenticationContext context, String configName) {

        String configValue = null;
        if ((context.getProperty(configName)) != null) {
            configValue = String.valueOf(context.getProperty(configName));
        }
        if (log.isDebugEnabled()) {
            String debugMessage = String.format("Config value for key %s: %s", configName, configValue);
            log.debug(debugMessage);
        }
        return configValue;
    }

    @Override
    protected void processAuthenticationResponse(HttpServletRequest httpServletRequest, HttpServletResponse
            httpServletResponse, AuthenticationContext authenticationContext) throws AuthenticationFailedException {

        AuthenticatedUser user = authenticationContext.getSequenceConfig().
                getStepMap().get(authenticationContext.getCurrentStep() - 1).getAuthenticatedUser();

        AuthenticationContext sessionContext = AuthContextCache
                .getInstance()
                .getValueFromCacheByRequestId(new AuthContextcacheKey(httpServletRequest
                        .getParameter(PushAuthenticatorConstants.SESSION_DATA_KEY))).getAuthenticationContext();

        AuthDataDTO authDataDTO = (AuthDataDTO) sessionContext
                .getProperty(PushAuthenticatorConstants.CONTEXT_AUTH_DATA);

        String jwt = authDataDTO.getAuthToken();
        String serverChallenge = authDataDTO.getChallenge();

        PushJWTValidator validator = new PushJWTValidator();
        try {
            if (validateSignature(jwt, serverChallenge)) {
                String authStatus = validator.getAuthStatus(jwt);
                // TODO: Change Successful to Allowed
                if (authStatus.equals(PushAuthenticatorConstants.AUTH_REQUEST_STATUS_SUCCESS)) {
                    authenticationContext.setSubject(user);
                } else if (authStatus.equals(PushAuthenticatorConstants.AUTH_REQUEST_STATUS_DENIED)) {
                    String deniedPage = URLEncoder
                            .encode("/authenticationendpoint/retry.do?status=Authentication Denied!",
                                    StandardCharsets.UTF_8.name()) + URLEncoder
                            .encode("&statusMsg=Authentication was denied from the mobile app",
                                    StandardCharsets.UTF_8.name());
                    httpServletResponse.sendRedirect(deniedPage);
                } else {
                    String errorMessage = String.format("Authentication failed! Auth status for user" +
                            " '%s' is not available in JWT.", user);
                    throw new AuthenticationFailedException(errorMessage);
                }
            } else {
                authenticationContext.setProperty(PushAuthenticatorConstants.AUTHENTICATION_STATUS, true);
                String errorMessage = String
                        .format("Authentication failed! JWT signature is not valid for device: %s of user: %s.",
                                validator.getDeviceId(jwt), user);
                throw new AuthenticationFailedException(errorMessage);
            }

        } catch (IOException e) {
            String errorMessage = String
                    .format("Error occurred when redirecting to the request denied page for device: %s of user: %s.",
                            validator.getDeviceId(jwt), user);
            throw new AuthenticationFailedException(errorMessage, e);
        } catch (PushAuthenticatorException e) {
            String errorMessage = String
                    .format("Error occurred when trying to validate the JWT signature from device: %s of user: %s.",
                            validator.getDeviceId(jwt), user);
            throw new AuthenticationFailedException(errorMessage, e);
        }

        AuthContextCache.getInstance().clearCacheEntryByRequestId(new AuthContextcacheKey(
                validator.getSessionDataKey(jwt)));
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
        serverKeyProperty.setRequired(false);
        serverKeyProperty.setConfidential(true);
        configProperties.add(serverKeyProperty);

        String fcmUrl = "Firebase url";
        Property fcmUrlProperty = new Property();
        fcmUrlProperty.setName(PushAuthenticatorConstants.FCM_URL);
        fcmUrlProperty.setDisplayName(fcmUrl);
        fcmUrlProperty.setDescription("Enter the url of firebase endpoint ");
        fcmUrlProperty.setDisplayOrder(1);
        fcmUrlProperty.setConfidential(true);
        configProperties.add(fcmUrlProperty);
        return configProperties;
    }

    /**
     * Send the authentication request to the mobile app using FCM
     *
     * @param request  HTTP Request
     * @param response HTTP Response
     * @param deviceId Device ID of the authenticating device
     * @param key      Session Data Key
     * @throws IOException
     */
    public void sendRequest(HttpServletRequest request, HttpServletResponse response,
                            String deviceId, String key) throws IOException, PushAuthenticatorException {

        DeviceHandler deviceHandler = new DeviceHandlerImpl();
        Device device = null;
        try {
            device = deviceHandler.getDevice(deviceId);
        } catch (PushDeviceHandlerClientException e) {
            String errorMessage = String.format("Error occurred when trying to get device: %s.", deviceId);
            throw new PushAuthenticatorException(errorMessage, e);
        } catch (SQLException e) {
            String errorMessage = String
                    .format("Error when trying to get device: %s from the database.", deviceId);
            throw new PushAuthenticatorException(errorMessage, e);
        } catch (PushDeviceHandlerServerException e) {
            String errorMessage = String
                    .format("Error occurred when trying to get device: %s. Device may not be registered.", deviceId);
            throw new PushAuthenticatorException(errorMessage, e);
        }
        AuthenticationContext context = AuthContextCache.getInstance().getValueFromCacheByRequestId
                (new AuthContextcacheKey(key)).getAuthenticationContext();

        AuthenticatedUser user = context.getSequenceConfig().getStepMap().
                get(context.getCurrentStep() - 1).getAuthenticatedUser();
        String username = user.getUserName();
        Map<String, String> authenticatorProperties = context.getAuthenticatorProperties();
        String serverKey = authenticatorProperties.get(PushAuthenticatorConstants.SERVER_KEY);
        String fcmUrl = authenticatorProperties.get(PushAuthenticatorConstants.FCM_URL);
        String hostname = request.getRemoteAddr();

        String serviceProviderName = context.getServiceProviderName();

        String message = username + " is requesting to log into " + serviceProviderName;
        String sessionDataKey = request.getParameter(InboundConstants.RequestProcessor.CONTEXT_KEY);
        UUID challenge = UUID.randomUUID();
        String randomChallenge = challenge.toString();
        AuthDataDTO authDataDTO = (AuthDataDTO) context.getProperty(PushAuthenticatorConstants.CONTEXT_AUTH_DATA);
        authDataDTO.setChallenge(randomChallenge);
        context.setProperty(PushAuthenticatorConstants.CONTEXT_AUTH_DATA, authDataDTO);
        AuthContextCache.getInstance().addToCacheByRequestId(new AuthContextcacheKey(key),
                new AuthContextCacheEntry(context));

        String pushId = device.getPushId();

        Map<String, String> userClaims = null;
        try {
            userClaims = getUserClaimValues(user, context);
        } catch (AuthenticationFailedException e) {
            String errorMessage = String
                    .format("Error occurred when retrieving user claims for user: %s.", user);
            throw new PushAuthenticatorException(errorMessage, e);
        }

        String fullName =
                userClaims.get(PushAuthenticatorConstants.FIRST_NAME_CLAIM) + " " +
                        userClaims.get(PushAuthenticatorConstants.LAST_NAME_CLAIM);
        String organization = user.getTenantDomain();

        String userAgentString = request.getHeader("user-agent");
        Parser uaParser = new Parser();
        Client uaClient = uaParser.parse(userAgentString);

        String userOS = uaClient.os.family;
        String userBrowser = uaClient.userAgent.family;

        FirebasePushNotificationSenderImpl pushNotificationSender = FirebasePushNotificationSenderImpl.getInstance();
        pushNotificationSender.init(serverKey, fcmUrl);
        try {
            pushNotificationSender.sendPushNotification(deviceId, pushId, message, randomChallenge, sessionDataKey,
                    username, fullName, organization, serviceProviderName, hostname, userOS, userBrowser);
        } catch (AuthenticationFailedException e) {
            String errorMessage = String
                    .format("Error occurred when trying to send the push notification to device: %s.", deviceId);
            throw new PushAuthenticatorException(errorMessage, e);
        }

        try {
            String waitPage = PushAuthenticatorConstants.WAIT_PAGE
                    + "?sessionDataKey="
                    + URLEncoder.encode(sessionDataKey, StandardCharsets.UTF_8.name())
                    + "&challenge="
                    + URLEncoder.encode(String.valueOf(challenge), StandardCharsets.UTF_8.name());
            response.sendRedirect(waitPage);
        } catch (IOException e) {
            String errorMessage = String
                    .format("Error occurred when trying to to redirect user: %s to the wait page.", user);
            throw new PushAuthenticatorException(errorMessage, e);
        }

    }

    /**
     * Validate the signature using the JWT received from mobile app
     *
     * @param jwt       JWT generated from mobile app
     * @param challenge Challenge stored in cache to correlate with JWT
     * @return Boolean for validity of the signature
     * @throws PushAuthenticatorException
     */
    private boolean validateSignature(String jwt, String challenge)
            throws PushAuthenticatorException {

        boolean isValid = false;
        DeviceHandler handler = new DeviceHandlerImpl();

        PushJWTValidator validator = new PushJWTValidator();
        String deviceId = validator.getDeviceId(jwt);
        String publicKeyStr = null;
        try {
            publicKeyStr = handler.getPublicKey(deviceId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            isValid = validator.validate(jwt, publicKeyStr, challenge);
        } catch (Exception e) {
            String errorMessage = "Error occurred when validating the signature. Failed to parse string to JWT.";
            throw new PushAuthenticatorException(errorMessage, e);
        }
        return isValid;
    }

    /**
     * Get the user claim values for required fields
     *
     * @param authenticatedUser Authenticated user
     * @param context           Authentication context
     * @return Retrieved user claims
     * @throws AuthenticationFailedException
     */
    private Map<String, String> getUserClaimValues(
            AuthenticatedUser authenticatedUser, AuthenticationContext context)
            throws AuthenticationFailedException {

        Map<String, String> claimValues;
        try {
            UserRealm userRealm = getUserRealm(authenticatedUser, context);
            UserStoreManager userStoreManager = userRealm.getUserStoreManager();
            claimValues = userStoreManager.getUserClaimValues(IdentityUtil.addDomainToName(
                    authenticatedUser.getUserName(), authenticatedUser.getUserStoreDomain()), new String[]{
                            PushAuthenticatorConstants.FIRST_NAME_CLAIM,
                            PushAuthenticatorConstants.LAST_NAME_CLAIM},
                    UserCoreConstants.DEFAULT_PROFILE);
        } catch (UserStoreException e) {
            String errorMessage = String.format("Failed to read user claims for user : %s.", authenticatedUser);
            throw new AuthenticationFailedException(errorMessage, e);
        }
        return claimValues;
    }

    /**
     * Get the user realm of the logged in user.
     *
     * @param authenticatedUser Authenticated user.
     * @return The userRealm.
     * @throws AuthenticationFailedException Exception on authentication failure.
     */
    private UserRealm getUserRealm(
            AuthenticatedUser authenticatedUser, AuthenticationContext context)
            throws AuthenticationFailedException {

        UserRealm userRealm = null;
        try {
            if (authenticatedUser != null) {
                String tenantDomain = authenticatedUser.getTenantDomain();
                int tenantId = IdentityTenantUtil.getTenantId(tenantDomain);
                RealmService realmService = PushAuthenticatorServiceComponent.getRealmService();
                userRealm = realmService.getTenantUserRealm(tenantId);
            }
        } catch (UserStoreException e) {
            String errorMessage = String
                    .format("Error occurred when trying to get the user realm for user: %s.", authenticatedUser);
            throw new AuthenticationFailedException(errorMessage, e);
        }
        return userRealm;
    }

}
