package org.wso2.carbon.identity.mobile.wso2verify.util.impl

import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

class BiometricAuthUtil {
    companion object{
        fun signChallenge(privateKey: String, challenge: String): String{
            var keyspec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey))
            var pk: PrivateKey = KeyFactory.getInstance("DSA").generatePrivate(keyspec)

            var sign = Signature.getInstance("Sha256withDSA")
            sign.initSign(pk)
            sign.update(challenge.toByteArray())

            return Base64.getEncoder().encodeToString(sign.sign())
        }
    }


}