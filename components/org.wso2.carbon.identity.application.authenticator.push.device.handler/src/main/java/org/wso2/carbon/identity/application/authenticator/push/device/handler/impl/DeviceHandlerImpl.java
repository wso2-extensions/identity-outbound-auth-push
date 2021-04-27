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
 */

package org.wso2.carbon.identity.application.authenticator.push.device.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandler;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandlerConstants;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.cache.PushDeviceHandlerCacheKey;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.cache.DeviceCache;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.cache.DeviceCacheEntry;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.cache.RegistrationRequestChallengeCache;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.cache.RegistrationRequestChallengeCacheEntry;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.dao.DeviceDAOImpl;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.Device;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.DiscoveryData;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.RegistrationRequest;
import org.wso2.carbon.identity.application.common.model.User;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.List;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * This class implements the DeviceHandler interface.
 */
public class DeviceHandlerImpl implements DeviceHandler, Serializable {

    private static final Log log = LogFactory.getLog(DeviceHandler.class);

    @Override
    public Device registerDevice(RegistrationRequest registrationRequest)
            throws IdentityException,
            UserStoreException, JsonProcessingException, NoSuchAlgorithmException,
            SignatureException, InvalidKeySpecException, InvalidKeyException {

        Device device;
        RegistrationRequestChallengeCacheEntry cacheEntry = RegistrationRequestChallengeCache.getInstance()
                .getValueFromCacheByRequestId(new PushDeviceHandlerCacheKey(registrationRequest.getDeviceId()));
        if (cacheEntry == null) {
            throw new PushDeviceHandlerServerException("Unidentified request for registration request challenge cache"
                    + "when trying to register device: " + registrationRequest.getDeviceId() + ".");
        }
        if (log.isDebugEnabled()) {
            log.debug("Verifying digital signature for device: " + registrationRequest.getDeviceId() + ".");
        }
        if (!verifySignature(registrationRequest.getSignature(), registrationRequest.getPushId(),
                registrationRequest.getPublicKey(), cacheEntry)) {
            throw new PushDeviceHandlerServerException("Could not verify signature to register device: "
                    + registrationRequest.getDeviceId() + ".");
        }
        if (!cacheEntry.isRegistered()) {
            String userId = getUserIdFromUsername(cacheEntry.getUsername(),
                    IdentityTenantUtil.getRealm(cacheEntry.getTenantDomain(), cacheEntry.getUsername()));
            device = new Device(registrationRequest.getDeviceId(), userId, registrationRequest.getDeviceName(),
                    registrationRequest.getDeviceModel(), registrationRequest.getPushId(),
                    registrationRequest.getPublicKey());
            DeviceCache.getInstance().addToCacheByRequestId(new PushDeviceHandlerCacheKey(device.getDeviceId()),
                    new DeviceCacheEntry(device));
            try {
                DeviceDAOImpl.getInstance().registerDevice(device);
            } catch (SQLException e) {
                throw new PushDeviceHandlerServerException("Error occurred when trying to register device: "
                        + registrationRequest.getDeviceId() + ".", e);
            }
        } else {
            String errorMessage = String.format("The device: %s is already registered.",
                    registrationRequest.getDeviceId());
            throw new PushDeviceHandlerClientException(errorMessage);
        }

        RegistrationRequestChallengeCache.getInstance().clearCacheEntryByRequestId(
                new PushDeviceHandlerCacheKey(registrationRequest.getDeviceId()));
        return device;
    }

    @Override
    public void unregisterDevice(String deviceId) throws PushDeviceHandlerServerException,
            PushDeviceHandlerClientException {

        try {
            DeviceDAOImpl.getInstance().unregisterDevice(deviceId);
        } catch (SQLException e) {
            String errorMessage = String.format("Error occurred when trying to remove device: %s from the"
                    + " database.", deviceId);
            throw new PushDeviceHandlerServerException(errorMessage, e);
        }

    }

    @Override
    public void editDeviceName(String deviceId, String newDeviceName) throws PushDeviceHandlerServerException {

        try {
            DeviceCacheEntry cacheEntry = DeviceCache.getInstance()
                    .getValueFromCacheByRequestId(new PushDeviceHandlerCacheKey(deviceId));
            if (cacheEntry != null) {
                if (!cacheEntry.getDevice().getDeviceName().equals(newDeviceName)) {
                    DeviceDAOImpl.getInstance().editDeviceName(deviceId, newDeviceName);
                }
                DeviceCache.getInstance().clearCacheEntryByRequestId(new PushDeviceHandlerCacheKey(deviceId));
            } else {
                DeviceDAOImpl.getInstance().editDeviceName(deviceId, newDeviceName);
            }
        } catch (SQLException e) {
            throw new PushDeviceHandlerServerException("Error occurred when updating the name of device: "
                    + deviceId + ".");
        }
    }

    @Override
    public Device getDevice(String deviceId) throws PushDeviceHandlerClientException, PushDeviceHandlerServerException {

        try {
            return DeviceDAOImpl.getInstance().getDevice(deviceId);
        } catch (SQLException e) {
            String errorMessage = String.format("Error occurred when trying to get device: %s from the database.",
                    deviceId);
            throw new PushDeviceHandlerServerException(errorMessage, e);
        }
    }

    @Override
    public List<Device> listDevices(String username, String userStore, String tenantDomain)
            throws PushDeviceHandlerServerException, PushDeviceHandlerClientException, UserStoreException {

        try {
            return DeviceDAOImpl.getInstance().listDevices(username, userStore, tenantDomain);
        } catch (SQLException e) {
            String errorMessage = String.format("Error occurred when trying to get the device list for user: %s"
                    + "from the database.", username);
            throw new PushDeviceHandlerServerException(errorMessage, e);
        }
    }

    @Override
    public DiscoveryData getDiscoveryData() {

        User user = getAuthenticatedUser();

        if (log.isDebugEnabled()) {
            log.debug("Retrieving data to generate QR code for user: " + user.toFullQualifiedUsername() + ".");
        }

        Map<String, String> userClaims = null;
        try {
            userClaims = getUserClaimValues(user);
        } catch (AuthenticationFailedException e) {
            log.error("Error occurred when trying to get the user clams for user: "
                    + user.toFullQualifiedUsername() + ".", e);
        }

        String deviceId = UUID.randomUUID().toString();
        String firstName = userClaims.get(DeviceHandlerConstants.GIVEN_NAME_USER_CLAIM);
        String lastName = userClaims.get(DeviceHandlerConstants.LAST_NAME_USER_CLAIM);
        String tenantDomain = user.getTenantDomain();
//        String host = "https://192.168.1.112:9443"; // Remove once host name prob is figured
        String host = IdentityUtil.getServerURL(null, false, false);
        String basePath = "/t/" + user.getTenantDomain() + "/api/users/v1/me";
        String registrationEndpoint = DeviceHandlerConstants.REGISTRATION_ENDPOINT;
        String removeDeviceEndpoint = DeviceHandlerConstants.REMOVE_DEVICE_ENDPOINT;
        String authenticationEndpoint = DeviceHandlerConstants.AUTHENTICATION_ENDPOINT;
        UUID challenge = UUID.randomUUID();
        RegistrationRequestChallengeCache.getInstance().addToCacheByRequestId
                (new PushDeviceHandlerCacheKey(deviceId), new RegistrationRequestChallengeCacheEntry(challenge,
                        user.getUserName(), user.getTenantDomain(), false));
        return new DiscoveryData(deviceId, user.getUserName(), firstName, lastName, tenantDomain, host, basePath,
                registrationEndpoint, removeDeviceEndpoint, authenticationEndpoint, challenge);
    }

    @Override
    public String getPublicKey(String deviceId) throws PushDeviceHandlerServerException {

        try {
            return DeviceDAOImpl.getInstance().getPublicKey(deviceId);
        } catch (SQLException e) {
            String errorMessage = String.format("Error occurred when trying to get the pubic key for device: %s "
                    + "from the database.", deviceId);
            throw new PushDeviceHandlerServerException(errorMessage, e);
        }
    }

    /**
     * Get the authenticated user
     *
     * @return authenticated user
     */
    private User getAuthenticatedUser() {

        User user = User.getUserFromUserName(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
        user.setTenantDomain(PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain());
        return user;
    }

    /**
     * Verify the signature using the public key for the registered device
     *
     * @param signature    signature of the signed challenge
     * @param pushId       pushID of the device
     * @param publicKeyStr public key for the registered device
     * @param cacheEntry   cached data
     * @return boolean verification
     * @throws NoSuchAlgorithmException Invalid algorithm
     * @throws InvalidKeySpecException  Invalid key specification
     * @throws InvalidKeyException      Invalid key
     * @throws SignatureException       Signature object is not initialised properly
     */
    private boolean verifySignature(String signature, String pushId, String publicKeyStr,
                                    RegistrationRequestChallengeCacheEntry cacheEntry)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {

        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        Signature sign = Signature.getInstance("SHA256withRSA");
        byte[] publicKeyData = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyData);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(spec);
        sign.initVerify(publicKey);
        sign.update((cacheEntry.getChallenge().toString() + "." + pushId).getBytes());
        return sign.verify(signatureBytes);
    }

    /**
     * Get the user ID from the username
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
     * Get the user claim values for required fields
     *
     * @param authenticatedUser Authenticated user
     * @return Retrieved user claims
     * @throws AuthenticationFailedException if reading user claims have errors
     */
    private Map<String, String> getUserClaimValues(User authenticatedUser)
            throws AuthenticationFailedException {

        Map<String, String> claimValues;
        try {
            UserRealm userRealm = CarbonContext.getThreadLocalCarbonContext().getUserRealm();
            UserStoreManager userStoreManager = userRealm.getUserStoreManager();
            claimValues = userStoreManager.getUserClaimValues(IdentityUtil.addDomainToName(
                    authenticatedUser.getUserName(), authenticatedUser.getUserStoreDomain()), new String[]{
                            DeviceHandlerConstants.GIVEN_NAME_USER_CLAIM,
                            DeviceHandlerConstants.LAST_NAME_USER_CLAIM},
                    UserCoreConstants.DEFAULT_PROFILE);
        } catch (UserStoreException e) {
            throw new AuthenticationFailedException("Error while reading user claims for user: "
                    + authenticatedUser.toFullQualifiedUsername() + ".", e);
        }
        return claimValues;
    }

}
