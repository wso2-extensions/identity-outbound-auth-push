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

import java.io.IOException;

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
import java.util.ArrayList;
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
            throws IdentityException, SQLException,
            UserStoreException, JsonProcessingException, NoSuchAlgorithmException,
            SignatureException, InvalidKeySpecException, InvalidKeyException {

        Device device = null;
        RegistrationRequestChallengeCacheEntry cacheEntry = RegistrationRequestChallengeCache.getInstance()
                .getValueFromCacheByRequestId(new PushDeviceHandlerCacheKey(registrationRequest.getDeviceId()));
        if (cacheEntry == null) {
            throw new PushDeviceHandlerServerException("Unidentified request");
        }
        if (log.isDebugEnabled()) {
            log.debug("Verifying digital signature");
        }
        if (!verifySignature(registrationRequest.getSignature(), registrationRequest.getPushId(),
                registrationRequest.getPublicKey(), cacheEntry)) {
            throw new PushDeviceHandlerServerException("Could not verify source");
        }
        if (!cacheEntry.isRegistered()) {
            String userId = getUserIdFromUsername(cacheEntry.getUsername(),
                    IdentityTenantUtil.getRealm(cacheEntry.getTenantDomain(), cacheEntry.getUsername()));
            device = new Device(registrationRequest.getDeviceId(), userId, registrationRequest.getDeviceName(),
                    registrationRequest.getDeviceModel(), registrationRequest.getPushId(),
                    registrationRequest.getPublicKey());
            DeviceCache.getInstance().addToCacheByRequestId(new PushDeviceHandlerCacheKey(device.getDeviceId()),
                    new DeviceCacheEntry(device));
            DeviceDAOImpl.getInstance().registerDevice(device);
        } else {
            throw new PushDeviceHandlerClientException("The device is already registered");
        }

        RegistrationRequestChallengeCache.getInstance().clearCacheEntryByRequestId(
                new PushDeviceHandlerCacheKey(registrationRequest.getDeviceId()));
        return device;
    }

    @Override
    public void unregisterDevice(String deviceId) throws PushDeviceHandlerServerException,
            PushDeviceHandlerClientException, SQLException {

        DeviceDAOImpl.getInstance().unregisterDevice(deviceId);

    }

    @Override
    public void editDeviceName(String deviceId, String newDeviceName) throws PushDeviceHandlerServerException,
            SQLException {

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
    }

    @Override
    public Device getDevice(String deviceId) throws PushDeviceHandlerClientException, SQLException,
            PushDeviceHandlerServerException, IOException {

        return DeviceDAOImpl.getInstance().getDevice(deviceId);
    }

    @Override
    public ArrayList<Device> listDevices(String username, String userStore, String tenantDomain)
            throws PushDeviceHandlerServerException,
            PushDeviceHandlerClientException, SQLException, UserStoreException, IOException {

        return DeviceDAOImpl.getInstance().listDevices(username, userStore, tenantDomain);
    }

    @Override
    public DiscoveryData getDiscoveryData() {

        if (log.isDebugEnabled()) {
            log.debug("Retrieving data to generate QR code");
        }
        User user = getAuthenticatedUser();

        Map<String, String> userClaims = null;
        try {
            userClaims = getUserClaimValues(user);
        } catch (AuthenticationFailedException e) {
            e.printStackTrace();
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
    public String getPublicKey(String deviceId) throws SQLException, IOException {

        return DeviceDAOImpl.getInstance().getPublicKey(deviceId);
    }

    private User getAuthenticatedUser() {

        User user = User.getUserFromUserName(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
        user.setTenantDomain(PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain());
        return user;
    }

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

    private String getUserIdFromUsername(String username, UserRealm realm) throws UserStoreException {

        AbstractUserStoreManager userStoreManager = (AbstractUserStoreManager) realm.getUserStoreManager();
        return userStoreManager.getUserIDFromUserName(username);
    }

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
            log.error("Error while reading user claims", e);
            String errorMessage = String.format("Failed to read user claims for user : %s.", authenticatedUser);
            throw new AuthenticationFailedException(errorMessage, e);
        }
        return claimValues;
    }

}
