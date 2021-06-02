package org.wso2.carbon.identity.application.authenticator.push.validator;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang.StringUtils;
//import org.wso2.carbon.core.util.KeyStoreManager;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

/**
 * JWT Access toke validator for Push authentication
 */
public class PushJWTValidator {

    private static final String DOT_SEPARATOR = ".";

    /**
     * Validate JWT
     *
     * @param jwt
     * @param publicKey
     * @param challenge
     * @return
     * @throws IdentityPushException
     */
    public boolean validate(String jwt, String publicKey, String challenge)
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
            } else {
                // Challenge correlation validation
                if (!claimsSet.getClaim("chg").equals(challenge)) {
                    return false;
                }
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
     * Validate the signature of the JWT
     *
     * @param publicKeyStr
     * @param signedJWT
     * @return
     */
    public static boolean validateSignature(String publicKeyStr, SignedJWT signedJWT) {

        try {
            byte[] publicKeyData = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyData);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(spec);

            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            return signedJWT.verify(verifier);

        } catch (Exception var10) {
            return false;
        }
    }

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
            e.printStackTrace();
            return null;
        }
    }

    public String getDeviceId(String jwt) {

        return getJWTClaim(jwt, "did");
    }

    public String getSessionDataKey(String jwt) {

        return getJWTClaim(jwt, "sid");
    }

    public String getAuthStatus(String jwt) {

        return getJWTClaim(jwt, "res");
    }

    private boolean checkExpirationTime(Date expirationTime) {

        long timeStampSkewMillis = OAuthServerConfiguration.getInstance().getTimeStampSkewInSeconds() * 1000;
        long expirationTimeInMillis = expirationTime.getTime();
        long currentTimeInMillis = System.currentTimeMillis();
        if ((currentTimeInMillis + timeStampSkewMillis) > expirationTimeInMillis) {
            return false;
        }

        return true;
    }

    private boolean checkNotBeforeTime(Date notBeforeTime) {

        if (notBeforeTime != null) {
            long timeStampSkewMillis = OAuthServerConfiguration.getInstance().getTimeStampSkewInSeconds() * 1000;
            long notBeforeTimeMillis = notBeforeTime.getTime();
            long currentTimeInMillis = System.currentTimeMillis();
            if (currentTimeInMillis + timeStampSkewMillis < notBeforeTimeMillis) {
                return false;
            }

        }
        return true;
    }

    /**
     * Validate legitimacy of JWT
     *
     * @param tokenIdentifier
     * @return
     */
    private boolean isJWT(String tokenIdentifier) {
        // JWT token contains 3 base64 encoded components separated by periods.
        return StringUtils.countMatches(tokenIdentifier, DOT_SEPARATOR) == 2;
    }

}