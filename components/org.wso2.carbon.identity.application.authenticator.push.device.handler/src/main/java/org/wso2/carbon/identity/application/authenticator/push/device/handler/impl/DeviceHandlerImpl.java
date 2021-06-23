/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandler;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandlerConstants;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.cache.PushDeviceHandlerCacheKey;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.cache.RegistrationRequestChallengeCache;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.cache.RegistrationRequestChallengeCacheEntry;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.dao.DeviceDAO;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.dao.DeviceDAOImpl;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.Device;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.RegistrationDiscoveryData;
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

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * This class implements the DeviceHandler interface.
 */
public class DeviceHandlerImpl implements DeviceHandler {

    private DeviceDAO deviceDAO;

    @Override
    public Device registerDevice(RegistrationRequest registrationRequest)
            throws PushDeviceHandlerServerException, PushDeviceHandlerClientException {

        RegistrationRequestChallengeCacheEntry cacheEntry = RegistrationRequestChallengeCache.getInstance()
                .getValueFromCacheByRequestId(new PushDeviceHandlerCacheKey(registrationRequest.getDeviceId()));

        if (cacheEntry == null) {
            String errorMessage = String.format(DeviceHandlerConstants.ErrorMessages
                    .ERROR_CODE_CACHE_ENTRY_NOT_FOUND.toString(), registrationRequest.getDeviceId());
            throw new PushDeviceHandlerClientException(errorMessage);
        }

        Device device;
        if (!cacheEntry.isDeviceAlreadyRegistered()) {
            handleSignatureVerification(registrationRequest, cacheEntry);
            device = handleDeviceRegistration(registrationRequest, cacheEntry);
            clearCache(registrationRequest.getDeviceId());
        } else {
            clearCache(registrationRequest.getDeviceId());
            String errorMessage = String.format(DeviceHandlerConstants.ErrorMessages
                    .ERROR_CODE_DEVICE_ALREADY_REGISTERED.toString(), registrationRequest.getDeviceId());
            throw new PushDeviceHandlerClientException(errorMessage);
        }

        return device;
    }

    @Override
    public void unregisterDevice(String deviceId)
            throws PushDeviceHandlerServerException, PushDeviceHandlerClientException {

        deviceDAO = new DeviceDAOImpl();
        try {
            Optional<Device> device = deviceDAO.getDevice(deviceId);
            if (device.isPresent()) {
                deviceDAO.unregisterDevice(deviceId);
            } else {
                String errorMessage = String.format(DeviceHandlerConstants.ErrorMessages
                        .ERROR_CODE_UNREGISTER_UNAVAILABLE_DEVICE.toString(), deviceId);
                throw new PushDeviceHandlerClientException(errorMessage);
            }
        } catch (SQLException e) {
            String errorMessage = String.format(
                    DeviceHandlerConstants.ErrorMessages.ERROR_CODE_UNREGISTER_DEVICE_FAILED.toString(), deviceId);
            throw new PushDeviceHandlerServerException(errorMessage, e);
        }

    }

    @Override
    public void removeUserDevices(String userId) throws PushDeviceHandlerServerException {

        deviceDAO = new DeviceDAOImpl();
        try {
            deviceDAO.deleteAllDevicesOfUser(userId);
        } catch (SQLException e) {
            String errorMessage = String.format(
                    DeviceHandlerConstants.ErrorMessages.ERROR_CODE_REMOVE_ALL_DEVICES_FAILED.toString(), userId);
            throw new PushDeviceHandlerServerException(errorMessage, e);
        }
    }

    @Override
    public void editDevice(String deviceId, String path, String value)
            throws PushDeviceHandlerServerException, PushDeviceHandlerClientException {

        Device device;
        try {
            device = getDevice(deviceId);
            handleEditDevice(device, path, value);
        } catch (PushDeviceHandlerClientException e) {
            String errorMessage = String.format(
                    DeviceHandlerConstants.ErrorMessages.ERROR_CODE_EDIT_DEVICE_NOT_FOUND.toString(), deviceId);
            throw new PushDeviceHandlerClientException(errorMessage, e);
        }
    }

    @Override
    public Device getDevice(String deviceId) throws PushDeviceHandlerServerException, PushDeviceHandlerClientException {

        deviceDAO = new DeviceDAOImpl();
        try {
            Optional<Device> device = deviceDAO.getDevice(deviceId);
            if (device.isPresent()) {
                return device.get();
            } else {
                String errorMessage = String.format(
                        DeviceHandlerConstants.ErrorMessages.ERROR_CODE_DEVICE_NOT_FOUND.toString(), deviceId);
                throw new PushDeviceHandlerClientException(errorMessage);
            }

        } catch (SQLException e) {
            String errorMessage = String.format(
                    DeviceHandlerConstants.ErrorMessages.ERROR_CODE_GET_DEVICE_FAILED.toString(), deviceId);
            throw new PushDeviceHandlerServerException(errorMessage, e);
        }
    }

    @Override
    public List<Device> listDevices(String userId) throws PushDeviceHandlerServerException {

        deviceDAO = new DeviceDAOImpl();
        try {
            return deviceDAO.listDevices(userId);
        } catch (SQLException e) {
            String errorMessage = String.format(
                    DeviceHandlerConstants.ErrorMessages.ERROR_CODE_DEVICE_LIST_NOT_FOUND.toString(), userId);
            throw new PushDeviceHandlerServerException(errorMessage, e);
        }
    }

    @Override
    public RegistrationDiscoveryData getRegistrationDiscoveryData() throws PushDeviceHandlerServerException {

        User user = getAuthenticatedUser();

        RegistrationDiscoveryData discoveryData = prepareDiscoveryData(user);
        addToCache(user, discoveryData);

        return discoveryData;
    }

    @Override
    public String getPublicKey(String deviceId)
            throws PushDeviceHandlerServerException, PushDeviceHandlerClientException {

        deviceDAO = new DeviceDAOImpl();
        try {
            Optional<String> publicKey = deviceDAO.getPublicKey(deviceId);
            if (publicKey.isPresent()) {
                return publicKey.get();
            } else {
                String errorMessage =
                        String.format(DeviceHandlerConstants
                                .ErrorMessages.ERROR_CODE_PUBLIC_KEY_NOT_FOUND.toString(), deviceId);
                throw new PushDeviceHandlerClientException(errorMessage);
            }
        } catch (SQLException e) {
            String errorMessage = String.format(
                    DeviceHandlerConstants.ErrorMessages.ERROR_CODE_GET_PUBLIC_KEY_FAILED.toString(), deviceId);
            throw new PushDeviceHandlerServerException(errorMessage, e);
        }
    }

    /**
     * Prepare the discovery data to be sent.
     *
     * @param user Authenticated user
     * @return Prepared discovery data for new device registration
     * @throws PushDeviceHandlerServerException - if an error occurs while getting user claims
     */
    private RegistrationDiscoveryData prepareDiscoveryData(User user) throws PushDeviceHandlerServerException {

        Map<String, String> userClaims;
        try {
            userClaims = getUserClaimValues(user);
        } catch (AuthenticationFailedException e) {
            String errorMessage = String.format(DeviceHandlerConstants.ErrorMessages
                    .ERROR_CODE_GET_USER_CLAIMS_FAILED.toString(), user.toFullQualifiedUsername());
            throw new PushDeviceHandlerServerException(errorMessage, e);
        }

        String deviceId = UUID.randomUUID().toString();

        String firstName = null;
        String lastName = null;
        if (userClaims != null) {
            firstName = userClaims.get(DeviceHandlerConstants.GIVEN_NAME_USER_CLAIM);
            lastName = userClaims.get(DeviceHandlerConstants.LAST_NAME_USER_CLAIM);
        }

        String tenantDomain = user.getTenantDomain();
        String host = IdentityUtil.getServerURL(null, false, false);
        String basePath = DeviceHandlerConstants.TENANT_QUALIFIED_PATH + user.getTenantDomain()
                + DeviceHandlerConstants.ME_API_PATH;
        String registrationEndpoint = DeviceHandlerConstants.REGISTRATION_ENDPOINT;
        String removeDeviceEndpoint = DeviceHandlerConstants.REMOVE_DEVICE_ENDPOINT;
        String authenticationEndpoint = DeviceHandlerConstants.AUTHENTICATION_ENDPOINT;
        String challenge = UUID.randomUUID().toString();

        return new RegistrationDiscoveryData(deviceId, user.getUserName(), firstName, lastName, tenantDomain, host,
                basePath, registrationEndpoint, removeDeviceEndpoint, authenticationEndpoint, challenge);
    }

    /**
     * Handle the signature verification and exceptions.
     *
     * @param registrationRequest Object holding data for registering a new device
     * @param cacheEntry          Stored cache for the registration instance
     * @throws PushDeviceHandlerClientException - if the signature is not valid
     * @throws PushDeviceHandlerServerException - if a server error occurs while validating the signature
     */
    private void handleSignatureVerification(
            RegistrationRequest registrationRequest, RegistrationRequestChallengeCacheEntry cacheEntry)
            throws PushDeviceHandlerClientException, PushDeviceHandlerServerException {

        try {
            if (!verifySignature(registrationRequest.getSignature(), registrationRequest.getPushId(),
                    registrationRequest.getPublicKey(), cacheEntry)) {
                String errorMessage = String.format(DeviceHandlerConstants
                        .ErrorMessages.ERROR_CODE_INVALID_SIGNATURE.toString(), registrationRequest.getDeviceId());
                throw new PushDeviceHandlerClientException(errorMessage);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            String errorMessage = String.format(DeviceHandlerConstants.ErrorMessages
                    .ERROR_CODE_SIGNATURE_VERIFICATION_FAILED.toString(), registrationRequest.getDeviceId());
            throw new PushDeviceHandlerServerException(errorMessage, e);
        }
    }

    /**
     * Handle process of registering the device.
     *
     * @param registrationRequest Object holding data for registering a new device
     * @param cacheEntry          Stored cache for the registration instance
     * @return The device object once the registration if the registration is successful
     * @throws PushDeviceHandlerServerException - if a server error occurs
     */
    private Device handleDeviceRegistration(
            RegistrationRequest registrationRequest, RegistrationRequestChallengeCacheEntry cacheEntry)
            throws PushDeviceHandlerServerException {

        String userId;
        try {
            userId = getUserIdFromUsername(cacheEntry.getUsername(),
                    IdentityTenantUtil.getRealm(cacheEntry.getTenantDomain(), cacheEntry.getUsername()));
        } catch (UserStoreException | IdentityException e) {
            String errorMessage = String.format(DeviceHandlerConstants.ErrorMessages
                    .ERROR_CODE_FAILED_TO_GET_USER_ID.toString(), registrationRequest.getDeviceId());
            throw new PushDeviceHandlerServerException(errorMessage, e);
        }

        Device device = new Device(registrationRequest.getDeviceId(), userId, registrationRequest.getDeviceName(),
                registrationRequest.getDeviceModel(), registrationRequest.getPushId(),
                registrationRequest.getPublicKey());

        deviceDAO = new DeviceDAOImpl();
        try {
            deviceDAO.registerDevice(device);
            cacheEntry.setRegistered(true);

        } catch (SQLException e) {
            String errorMessage = String.format(DeviceHandlerConstants.ErrorMessages
                    .ERROR_CODE_REGISTER_DEVICE_FAILED.toString(), registrationRequest.getDeviceId());
            throw new PushDeviceHandlerServerException(errorMessage, e);
        }

        return device;
    }

    /**
     * Handle validations and complete the edit device process.
     *
     * @param device Device information stored in system
     * @param path   Path for thr attribute to be updated
     * @param value  New value that should be added
     * @throws PushDeviceHandlerServerException - if an error occurs while storing data in the database
     */
    private void handleEditDevice(Device device, String path, String value)
            throws PushDeviceHandlerServerException, PushDeviceHandlerClientException {

        deviceDAO = new DeviceDAOImpl();
        try {
            switch (path) {
                case "/device-name":
                    device.setDeviceName(value);
                    break;
                case "/push-id":
                    device.setPushId(value);
                    break;
                default:
                    throw new PushDeviceHandlerClientException("Invalid path for updating device: "
                            + device.getDeviceId() + ".");
            }

            deviceDAO.editDevice(device.getDeviceId(), device);
        } catch (SQLException e) {
            String errorMessage = String.format(DeviceHandlerConstants
                    .ErrorMessages.ERROR_CODE_EDIT_DEVICE_FAILED.toString(), device.getDeviceId());
            throw new PushDeviceHandlerServerException(errorMessage, e);
        }
    }

    /**
     * Add registration data to cache.
     *
     * @param user          Authenticated user
     * @param discoveryData Generated discovery data for the instance
     */
    private void addToCache(User user, RegistrationDiscoveryData discoveryData) {

        RegistrationRequestChallengeCache.getInstance().addToCacheByRequestId(
                new PushDeviceHandlerCacheKey(discoveryData.getDeviceId()),
                new RegistrationRequestChallengeCacheEntry(discoveryData.getChallenge(), user.getUserName(),
                        user.getTenantDomain(), false));
    }

    /**
     * Clear stored cache for device registration instance.
     *
     * @param deviceID Unique ID for new device
     */
    private void clearCache(String deviceID) {

        RegistrationRequestChallengeCache.getInstance().clearCacheEntryByRequestId(
                new PushDeviceHandlerCacheKey(deviceID));
    }

    /**
     * Get the authenticated user.
     *
     * @return authenticated user
     */
    private User getAuthenticatedUser() {

        User user = User.getUserFromUserName(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
        user.setTenantDomain(PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain());
        return user;
    }

    /**
     * Verify the signature using the public key for the registered device.
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
        Signature sign = Signature.getInstance(DeviceHandlerConstants.HASHING_ALGORITHM);
        byte[] publicKeyData = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyData);
        KeyFactory kf = KeyFactory.getInstance(DeviceHandlerConstants.SIGNATURE_ALGORITHM);
        PublicKey publicKey = kf.generatePublic(spec);
        sign.initVerify(publicKey);
        sign.update((cacheEntry.getChallenge() + "." + pushId).getBytes(StandardCharsets.UTF_8));
        return sign.verify(signatureBytes);
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
     * Get the user claim values for required fields.
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
