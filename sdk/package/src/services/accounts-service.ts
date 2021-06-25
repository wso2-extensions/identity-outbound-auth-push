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

import {KJUR} from "jsrsasign";
import uuid from "uuid-random";
import {AccountsInterface, DiscoveryDataInterface, RegistrationRequestInterface} from "../models";
import {CryptoUtil, DeviceInfoUtil, RequestSenderUtil} from "../utils";

/**
 * Class for all the functionality related to accounts.
 */
export class AccountsService {

    /**
     * Enrol the device with the WSO2 Identity Server.
     *
     * @param regRequest Body of the scanned QR code
     * @param fcmToken Firebase push authentication token
     */
    public async addAccount(regRequest: any, fcmToken: string): Promise<any> {

        const discoveryData = this.processDiscoveryData(regRequest);

        const keypair: any = CryptoUtil.generateKeypair();
        const signatureString = regRequest.chg + "." + fcmToken;
        let signedChallenge: string;

        try {
            signedChallenge = CryptoUtil.signChallenge(
                keypair.prvKey,
                signatureString
            );
        } catch (err) {

            return JSON.stringify({data: null, res: "FAILED"});
        }

        const modPubKey: string = keypair.pubKey
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace(/(\r\n|\n|\r)/gm, "");

        const request: RegistrationRequestInterface = {
            deviceId: discoveryData.deviceId,
            publicKey: modPubKey,
            pushID: fcmToken,
            signature: signedChallenge
        };

        request.deviceName = DeviceInfoUtil.getDeviceName();
        request.model = DeviceInfoUtil.getDeviceModel();

        const regRequestBody: any = {
            deviceId: request.deviceId,
            model: request.model,
            name: request.deviceName,
            publicKey: request.publicKey,
            pushId: request.pushID,
            signature: request.signature
        };

        const requestMethod = "POST";

        const headers = {
            Accept: "application/json",
            "Content-Type": "application/json"
        };

        const newRequest: RequestSenderUtil = new RequestSenderUtil();
        const registrationUrl =
            discoveryData.host +
            discoveryData.basePath +
            discoveryData.registrationEndpoint;

        return newRequest
            .sendRequest(
                registrationUrl,
                requestMethod,
                headers,
                JSON.stringify(regRequestBody)
            )
            .then((result) => {
                let account: AccountsInterface;
                if (result.status === 201) {
                    account = {
                        deviceID: request.deviceId,
                        username: discoveryData.username,
                        lastName: discoveryData.lastName,
                        tenantDomain: discoveryData.tenantDomain,
                        host: discoveryData.host,
                        basePath: discoveryData.basePath,
                        authenticationEndpoint: discoveryData.authenticationEndpoint,
                        removeDeviceEndpoint: discoveryData.removeDeviceEndpoint,
                        privateKey: keypair.prvKey
                    };

                    return JSON.stringify({res: "OK", data: account});
                } else {

                    return JSON.stringify({res: "FAILED", data: null});
                }


            });
    }

    /**
     * Revoke the enrollment of the device from the Identity Server.
     *
     * @param account User account to be removed
     */
    public async removeAccount(
        account: AccountsInterface
    ): Promise<string> {

        const jwt = KJUR.jws.JWS.sign(
            null,
            {
                alg: "RS256",
                did: account.deviceID
            } as any,
            {
                jti: uuid(),
                sub: account.username + "@" + account.tenantDomain,
                iss: "wso2verify",
                aud: account.host + "/t/" + account.tenantDomain + "/",
                nbf: KJUR.jws.IntDate.get("now"),
                exp: KJUR.jws.IntDate.get("now + 1hour"),
                iat: KJUR.jws.IntDate.get("now"),
                act: "REMOVE"
            },
            account.privateKey
        );

        const body = {
            token: jwt
        };

        const url = account.host + account.basePath + "/push-auth/devices/" + account.deviceID + "/remove";
        const headers = {
            "Content-Type": "application/json"
        };
        const requestMethod = "POST";

        const request: RequestSenderUtil = new RequestSenderUtil();

        return request.sendRequest(url, requestMethod, headers, JSON.stringify(body))
            .then((res) => {
                if (res.status === 204) {

                    return JSON.stringify({res: "SUCCESS", data: account});
                } else {

                    return JSON.stringify({res: "FAILED", data: account});
                }
            });
    }

    /**
     * Organizes the data into a complex object.
     *
     * @param regRequest JSON string from the QR code
     */
    private processDiscoveryData(regRequest: any): DiscoveryDataInterface {

        let discoveryData: DiscoveryDataInterface;

        if (
            regRequest.did &&
            regRequest.un &&
            regRequest.hst &&
            regRequest.bp &&
            regRequest.chg
        ) {
            discoveryData = {
                tenantDomain: regRequest.td,
                deviceId: regRequest.did,
                username: regRequest.un,
                host: regRequest.hst,
                basePath: regRequest.bp,
                registrationEndpoint: regRequest.re,
                authenticationEndpoint: regRequest.ae,
                removeDeviceEndpoint: regRequest.rde,
                challenge: regRequest.chg
            };
        } else {

            throw new Error("One or more required parameters (deviceId, username, host, basePath, challenge) "
                + "was not found.");
        }

        if (regRequest.fn) {
            discoveryData.firstName = regRequest.fn;
        }

        if (regRequest.ln) {
            discoveryData.lastName = regRequest.ln;
        }

        return discoveryData;
    }
}
