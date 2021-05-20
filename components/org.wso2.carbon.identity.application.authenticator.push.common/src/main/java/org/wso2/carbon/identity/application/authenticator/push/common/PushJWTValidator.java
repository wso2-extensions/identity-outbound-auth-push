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

package org.wso2.carbon.identity.application.authenticator.push.common;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authenticator.push.common.exception.PushAuthTokenValidationException;
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
     * @throws PushAuthTokenValidationException
     */
    public JWTClaimsSet validate(String jwt, String publicKey)
            throws PushAuthTokenValidationException {

        if (!isJWT(jwt)) {
            throw new PushAuthTokenValidationException("Token is not a valid JWT.");
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(jwt);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            if (claimsSet == null) {
                throw new PushAuthTokenValidationException("Token validation failed. Claim values were not found.");
            }

            if (!validateSignature(publicKey, signedJWT)) {
                throw new PushAuthTokenValidationException("Token signature validation failed.");
            }

            if (!checkExpirationTime(claimsSet.getExpirationTime())) {
                throw new PushAuthTokenValidationException("Token validation failed. JWT is expired.");
            }
            if (!checkNotBeforeTime(claimsSet.getNotBeforeTime())) {
                throw new PushAuthTokenValidationException("Token validation failed. JWT is not active.");
            }

            return claimsSet;
        } catch (ParseException e) {
            throw new PushAuthTokenValidationException("Error occurred while validating jwt", e);
        }
    }

    /**
     * Validate the signature of the JWT.
     *
     * @param publicKeyStr Public key for used for signing the JWT
     * @param signedJWT    Signed JWT
     * @return Boolean value for signature validation
     * @throws PushAuthTokenValidationException Error when validating the signature
     */
    private boolean validateSignature(String publicKeyStr, SignedJWT signedJWT)
            throws PushAuthTokenValidationException {

        try {
            byte[] publicKeyData = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyData);
            KeyFactory kf = KeyFactory.getInstance(PushAuthCommonConstants.SIGNING_ALGORITHM);
            RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(spec);

            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            return signedJWT.verify(verifier);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | JOSEException e) {
            throw new PushAuthTokenValidationException("Error occurred when validating token signature.", e);
        }
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
     */
    private boolean isJWT(String tokenIdentifier) {

        if (StringUtils.isBlank(tokenIdentifier)) {
            return false;
        }
        if (StringUtils.countMatches(tokenIdentifier, DOT_SEPARATOR) != 2) {
            return false;
        }
        try {
            JWTParser.parse(tokenIdentifier);
            return true;
        } catch (ParseException e) {
            if (log.isDebugEnabled()) {
                log.debug("Provided token identifier is not a parsable JWT.", e);
            }
            return false;
        }
    }

}
