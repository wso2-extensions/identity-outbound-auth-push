package org.wso2.carbon.identity.application.authenticator.push.notification.handler.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.inbound.InboundConstants;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authenticator.push.PushAuthenticatorConstants;
import org.wso2.carbon.identity.application.authenticator.push.cache.AuthContextCache;
import org.wso2.carbon.identity.application.authenticator.push.cache.AuthContextCacheEntry;
import org.wso2.carbon.identity.application.authenticator.push.cache.AuthContextcacheKey;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandler;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.impl.DeviceHandlerImpl;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.Device;
import org.wso2.carbon.identity.application.authenticator.push.dto.AuthDataDTO;
import org.wso2.carbon.identity.application.authenticator.push.exception.PushAuthenticatorException;
import org.wso2.carbon.identity.application.authenticator.push.internal.PushAuthenticatorServiceComponent;
import org.wso2.carbon.identity.application.authenticator.push.notification.handler.RequestSender;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.service.RealmService;
import ua_parser.Client;
import ua_parser.Parser;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implements the functionality for request sender
 */
public class RequestSenderImpl implements RequestSender {

    private static final Log log = LogFactory.getLog(RequestSenderImpl.class);

    @Override
    public void sendRequest(HttpServletRequest request, HttpServletResponse response, String deviceId, String key)
            throws PushAuthenticatorException {

        DeviceHandler deviceHandler = new DeviceHandlerImpl();
        Device device;
        try {
            device = deviceHandler.getDevice(deviceId);
        } catch (PushDeviceHandlerClientException e) {
            throw new PushAuthenticatorException("Error occurred when trying to get device: " + deviceId + ".", e);
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

        Map<String, String> userClaims;
        try {
            userClaims = getUserClaimValues(user);
        } catch (AuthenticationFailedException e) {
            throw new PushAuthenticatorException("Error occurred when retrieving user claims for user: "
                    + user.toFullQualifiedUsername() + ".", e);
        }

        String fullName =
                userClaims.get(PushAuthenticatorConstants.FIRST_NAME_CLAIM) + " " +
                        userClaims.get(PushAuthenticatorConstants.LAST_NAME_CLAIM);
        String organization = user.getTenantDomain();

        String userAgentString = request.getHeader("user-agent");
        Parser uaParser;
        String userOS = null;
        String userBrowser = null;
        try {
            uaParser = new Parser();
            Client uaClient = uaParser.parse(userAgentString);
            userOS = uaClient.os.family;
            userBrowser = uaClient.userAgent.family;
        } catch (IOException e) {
            log.error("Error occurred while trying to get the user's OS or Web browser.", e);
        }

        FirebasePushNotificationSenderImpl pushNotificationSender = FirebasePushNotificationSenderImpl.getInstance();
        pushNotificationSender.init(serverKey, fcmUrl);
        try {
            pushNotificationSender.sendPushNotification(deviceId, pushId, message, randomChallenge, sessionDataKey,
                    username, fullName, organization, serviceProviderName, hostname, userOS, userBrowser);
        } catch (AuthenticationFailedException e) {
            throw new PushAuthenticatorException("Error occurred when trying to send the push notification to device: "
                    + deviceId + ".", e);
        }

        try {
            String waitPage = PushAuthenticatorConstants.WAIT_PAGE
                    + "?sessionDataKey="
                    + URLEncoder.encode(sessionDataKey, StandardCharsets.UTF_8.name())
                    + "&challenge="
                    + URLEncoder.encode(String.valueOf(challenge), StandardCharsets.UTF_8.name());
            response.sendRedirect(waitPage);
        } catch (IOException e) {
            String errorMessage = String.format("Error occurred when trying to to redirect user: %s to the wait page.",
                    user.toFullQualifiedUsername());
            throw new PushAuthenticatorException(errorMessage, e);
        }

    }

    /**
     * Get the user claim values for required fields
     *
     * @param authenticatedUser Authenticated user
     * @return Retrieved user claims
     * @throws AuthenticationFailedException
     */
    private Map<String, String> getUserClaimValues(AuthenticatedUser authenticatedUser)
            throws AuthenticationFailedException {

        Map<String, String> claimValues;
        try {
            UserRealm userRealm = getUserRealm(authenticatedUser);
            UserStoreManager userStoreManager = userRealm.getUserStoreManager();
            claimValues = userStoreManager.getUserClaimValues(IdentityUtil.addDomainToName(
                    authenticatedUser.getUserName(), authenticatedUser.getUserStoreDomain()), new String[]{
                            PushAuthenticatorConstants.FIRST_NAME_CLAIM,
                            PushAuthenticatorConstants.LAST_NAME_CLAIM},
                    UserCoreConstants.DEFAULT_PROFILE);
        } catch (UserStoreException e) {
            throw new AuthenticationFailedException("Failed to read user claims for user : "
                    + authenticatedUser.toFullQualifiedUsername() + ".", e);
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
    public UserRealm getUserRealm(AuthenticatedUser authenticatedUser) throws AuthenticationFailedException {

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
}
