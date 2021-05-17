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

package org.wso2.carbon.identity.application.authenticator.push.core;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authenticator.push.core.exception.IdentityPushException;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

/**
 * JWT Access toke validator for Push authentication.
 */
public class PushJWTValidator {

    private static final String DOT_SEPARATOR = ".";
    private static final Log log = LogFactory.getLog(PushJWTValidator.class);

    /**
     * Validate JWT.
     *
     * @param jwt       JWT to be validated
     * @param publicKey Public key the JWT has been signed with
     * @return Boolean value for validation
     * @throws IdentityPushException
     */
    public boolean validate(String jwt, String publicKey)
            throws IdentityPushException {

        if (!isJWT(jwt)) {
            return false;
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(jwt);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            if (claimsSet == null) {
                throw new IdentityPushException("Claim values are empty in the given jwt.");
            }

            // JWT Signature validation
            if (!validateSignature(publicKey, signedJWT)) {
                return false;
            }

            // Validation of expiry time
            if (!checkExpirationTime(claimsSet.getExpirationTime())) {
                return false;
            }
            // Validation of active time
            if (!checkNotBeforeTime(claimsSet.getNotBeforeTime())) {
                return false;
            }
        } catch (ParseException e) {
            throw new IdentityPushException("Error while validating jwt", e);
        }
        return true;
    }

    /**
     * Validate the signature of the JWT.
     *
     * @param publicKeyStr Public key for used for signing the JWT
     * @param signedJWT    Signed JWT
     * @return Boolean value for signature validation
     * @throws IdentityPushException Error when validating the signature
     */
    public static boolean validateSignature(String publicKeyStr, SignedJWT signedJWT) throws IdentityPushException {

        try {
            byte[] publicKeyData = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyData);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(spec);

            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            return signedJWT.verify(verifier);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | JOSEException e) {
            throw new IdentityPushException("Error occurred when validating token signature.", e);
        }
    }

    /**
     * Get claim values from JWT by name.
     *
     * @param jwt   JWT string
     * @param claim Name of the required claim
     * @return Retrieved claim
     */
    public String getJWTClaim(String jwt, String claim) {

        try {
            if (!isJWT(jwt)) {
                return null;
            } else {
                SignedJWT signedJWT = SignedJWT.parse(jwt);
                JWTClaimsSet claimSet = signedJWT.getJWTClaimsSet();
                if (claimSet != null) {
                    return (String) claimSet.getClaim(claim);
                } else {
                    return null;
                }
            }
        } catch (ParseException e) {
            if (!log.isDebugEnabled()) {
                log.error("Error occurred while parsing JWT string", e);
            }
            return null;
        }
    }

    /**
     * Get Device ID from JWT.
     *
     * @param jwt JWT string
     * @return Device ID
     */
    public String getDeviceId(String jwt) {

        return getJWTClaim(jwt, "did");
    }

    /**
     * Get Session data key from JWT.
     *
     * @param jwt JWT string
     * @return Session data key
     */
    public String getSessionDataKey(String jwt) {

        return getJWTClaim(jwt, "sid");
    }

    /**
     * Get Authentication status from JWT.
     *
     * @param jwt JWT string
     * @return Authentication status
     */
    public String getAuthStatus(String jwt) {

        return getJWTClaim(jwt, "res");
    }

    /**
     * Validate if the JWT is expired.
     *
     * @param expirationTime Time set for the JWT to expire
     * @return Boolean validating if the JWT is not expired
     */
    private boolean checkExpirationTime(Date expirationTime) {

        long timeStampSkewMillis = OAuthServerConfiguration.getInstance().getTimeStampSkewInSeconds() * 1000;
        long expirationTimeInMillis = expirationTime.getTime();
        long currentTimeInMillis = System.currentTimeMillis();
        return (currentTimeInMillis + timeStampSkewMillis) <= expirationTimeInMillis;
    }

    /**
     * Validate if the JWT is active.
     *
     * @param notBeforeTime Time set to activate the JWT
     * @return Boolean validating if the JWT is active
     */
    private boolean checkNotBeforeTime(Date notBeforeTime) {

        if (notBeforeTime != null) {
            long timeStampSkewMillis = OAuthServerConfiguration.getInstance().getTimeStampSkewInSeconds() * 1000;
            long notBeforeTimeMillis = notBeforeTime.getTime();
            long currentTimeInMillis = System.currentTimeMillis();
            return currentTimeInMillis + timeStampSkewMillis >= notBeforeTimeMillis;

        } else {
            return false;
        }
    }

    /**
     * Validate legitimacy of JWT.
     *
     * @param tokenIdentifier JWT string
     * @return Boolean validation for if the string is a JWT
     */
    private boolean isJWT(String tokenIdentifier) {

        // JWT token contains 3 base64 encoded components separated by periods.
        return StringUtils.countMatches(tokenIdentifier, DOT_SEPARATOR) == 2;
    }

}
