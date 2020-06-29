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