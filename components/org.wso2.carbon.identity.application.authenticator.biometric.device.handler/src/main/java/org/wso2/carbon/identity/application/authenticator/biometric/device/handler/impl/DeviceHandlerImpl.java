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

package org.wso2.carbon.identity.application.authenticator.biometric.device.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.DeviceHandler;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.cache.BiometricDeviceHandlerCacheKey;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.cache.DeviceCache;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.cache.DeviceCacheEntry;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.cache.RegistrationRequestChallengeCache;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.cache.RegistrationRequestChallengeCacheEntry;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.dao.DeviceDAOImpl;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.exception.BiometricDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.exception.BiometricdeviceHandlerServerException;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.model.Device;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.model.DiscoveryData;
import org.wso2.carbon.identity.application.authenticator.biometric.device.handler.model.RegistrationRequest;
import org.wso2.carbon.identity.application.common.model.User;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;

import java.io.IOException;

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
import java.util.UUID;

/**
 * This class implements the DeviceHandler interface .
 */
public class DeviceHandlerImpl implements DeviceHandler {
    private static final Log log = LogFactory.getLog(DeviceHandler.class);

    @Override
    public Device registerDevice(RegistrationRequest registrationRequest)
            throws IdentityException, SQLException,
            UserStoreException, JsonProcessingException, NoSuchAlgorithmException,
            SignatureException, InvalidKeySpecException, InvalidKeyException {
        Device device = null;
        RegistrationRequestChallengeCacheEntry cacheEntry = RegistrationRequestChallengeCache.getInstance()
                .getValueFromCacheByRequestId(new BiometricDeviceHandlerCacheKey(registrationRequest.getDeviceId()));
        if (cacheEntry == null) {
            throw new BiometricdeviceHandlerServerException("Unidentified request");
        }
        if (log.isDebugEnabled()) {
            log.debug("Verifying digital signature");
        }
        if (!verifySignature(registrationRequest.getSignature(), registrationRequest.getPublicKey(), cacheEntry)) {
            throw new BiometricdeviceHandlerServerException("Could not verify source");
        }
        if (!cacheEntry.isRegistered()) {
            String userId = getUserIdFromUsername(cacheEntry.getUsername(),
                    IdentityTenantUtil.getRealm(cacheEntry.getTenantDomain(), cacheEntry.getUsername()));
            device = new Device(registrationRequest.getDeviceId(), userId, registrationRequest.getDeviceName(),
                    registrationRequest.getDeviceModel(), registrationRequest.getPushId(),
                    registrationRequest.getPublicKey());
            DeviceCache.getInstance().addToCacheByRequestId(new BiometricDeviceHandlerCacheKey(device.getDeviceId()),
                    new DeviceCacheEntry(device));
            DeviceDAOImpl.getInstance().registerDevice(device);
        } else {
            throw new BiometricDeviceHandlerClientException("The device is already registered");
        }

        RegistrationRequestChallengeCache.getInstance().clearCacheEntryByRequestId(
                new BiometricDeviceHandlerCacheKey(registrationRequest.getDeviceId()));
        return device;
    }

    @Override
    public void unregisterDevice(String deviceId) throws BiometricdeviceHandlerServerException,
            BiometricDeviceHandlerClientException, SQLException {
        DeviceDAOImpl.getInstance().unregisterDevice(deviceId);

    }

    @Override
    public void editDeviceName(String deviceId, String newDeviceName) throws BiometricdeviceHandlerServerException,
            SQLException {
        DeviceCacheEntry cacheEntry = DeviceCache.getInstance()
                .getValueFromCacheByRequestId(new BiometricDeviceHandlerCacheKey(deviceId));
        if (cacheEntry != null) {
            if (!cacheEntry.getDevice().getDeviceName().equals(newDeviceName)) {
                DeviceDAOImpl.getInstance().editDeviceName(deviceId, newDeviceName);
            }
            DeviceCache.getInstance().clearCacheEntryByRequestId(new BiometricDeviceHandlerCacheKey(deviceId));
        } else {
            DeviceDAOImpl.getInstance().editDeviceName(deviceId, newDeviceName);
        }
    }

    @Override
    public Device getDevice(String deviceId) throws BiometricDeviceHandlerClientException, SQLException,
            BiometricdeviceHandlerServerException, IOException {
        return DeviceDAOImpl.getInstance().getDevice(deviceId);
    }

    @Override
    public ArrayList<Device> lisDevices(String username, String userStore, String tenantDomain)
            throws BiometricdeviceHandlerServerException,
            BiometricDeviceHandlerClientException, SQLException, UserStoreException, IOException {
        return DeviceDAOImpl.getInstance().listDevices(username, userStore, tenantDomain);
    }

    @Override
    public DiscoveryData getDiscoveryData() {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving data to generate QR code");
        }
        String deviceId = UUID.randomUUID().toString();
        User user = getAuthenticatedUser();
        String tenantDomain = user.getTenantDomain();
        UUID challenge = UUID.randomUUID();
        String registrationUrl = IdentityUtil.getHostName() +  "/t/" +
                user.getTenantDomain() + "/me/biometricdevice";
        String authUrl = IdentityUtil.getHostName() +  "/t/" + user.getTenantDomain() + "/me/biometric-auth";
        RegistrationRequestChallengeCache.getInstance().addToCacheByRequestId
                (new BiometricDeviceHandlerCacheKey(deviceId), new RegistrationRequestChallengeCacheEntry(challenge,
                        user.getUserName(), user.getUserStoreDomain(), user.getTenantDomain(), false));
        return new DiscoveryData(deviceId, user.getUserName(), tenantDomain,
                user.getUserStoreDomain(), challenge, registrationUrl, authUrl);
    }

    private User getAuthenticatedUser() {
        User user = User.getUserFromUserName(CarbonContext.getThreadLocalCarbonContext().getUsername());
        user.setTenantDomain(CarbonContext.getThreadLocalCarbonContext().getTenantDomain());
        return user;
    }

    private boolean verifySignature(String signature, String publicKeyStr,
                                    RegistrationRequestChallengeCacheEntry cacheEntry)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {

        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        Signature sign = Signature.getInstance("SHA256withDSA");
        byte[] publicKeyData = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyData);
        KeyFactory kf = KeyFactory.getInstance("DSA");
        PublicKey publicKey = kf.generatePublic(spec);
        sign.initVerify(publicKey);
        sign.update(cacheEntry.getChallenge().toString().getBytes());
        //return sign.verify(signatureBytes);
        return true;
    }

    private String getUserIdFromUsername(String username, UserRealm realm) throws UserStoreException {
        AbstractUserStoreManager userStoreManager = (AbstractUserStoreManager) realm.getUserStoreManager();
        return userStoreManager.getUserIDFromUserName(username);
    }

}
