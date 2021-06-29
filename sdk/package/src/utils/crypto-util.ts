/**
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {KEYUTIL, KJUR, hextob64} from "jsrsasign";

/**
 * Util class handling crypto related functions.
 */
export class CryptoUtil {

    /**
     * Generate new keypair.
     *
     * @returns keypair - Generated keypair object containing PEM strings
     */
    public static generateKeypair(): any {

        const keyPair = KEYUTIL.generateKeypair("RSA", 1024);
        return {
            pubKey: KEYUTIL.getPEM(keyPair.pubKeyObj),
            prvKey: KEYUTIL.getPEM(keyPair.prvKeyObj, "PKCS8PRV")
        };
    }

    /**
     * Signs the challenge and returns the signed string.
     *
     * @param privateKey Private key to sign the challenge
     * @param challenge String containing the challenge
     *
     * @returns signedChallenge - A base64 string of the signed challenge
     */
    public static signChallenge(privateKey: string, challenge: string): string {
        try {

            const prvKey = KEYUTIL.getKey(privateKey);
            const sig: any = new KJUR.crypto.Signature({alg: "SHA256withRSA"});
            sig.init(prvKey);
            const signature = sig.signString(challenge);

            return hextob64(signature);
        } catch (err) {
            throw new Error("Error occurred when trying to sign the challenge string.")
        }
    }
}
