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

import {RegistrationRequestInterface} from "../models/registration-request";
import {AccountsInterface} from "../models/accounts";
import {DiscoveryDataInterface} from "../models/discovery-data";
import {DeviceInfoUtil} from "../utils/device-info-util";
import {CryptoUtil} from "../utils/crypto-util";
import uuid from "uuid-random";
import {RequestSenderUtil} from "../utils/request-sender-util";
import AsyncStorage from "@react-native-async-storage/async-storage";
import {KJUR} from "jsrsasign";

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

        console.log("Add Account function");
        let discoveryData = this.processDiscoveryData(regRequest);
        console.log("Discovery Data Processed");

        let keypair: any = CryptoUtil.generateKeypair();
        let signatureString = regRequest.chg + "." + fcmToken;
        console.log("Keypair:", keypair);
        let signedChallenge: string = CryptoUtil.signChallenge(
            keypair.prvKey,
            signatureString
        );

        let modPubKey: string = keypair.pubKey
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace(/(\r\n|\n|\r)/gm, "");

        let request: RegistrationRequestInterface;
        request = {
            deviceId: discoveryData.deviceId,
            pushID: fcmToken,
            publicKey: modPubKey,
            signature: signedChallenge,
        };

        request.deviceName = DeviceInfoUtil.getDeviceName();
        request.model = DeviceInfoUtil.getDeviceModel();

        let regRequestBody: any = {
            deviceId: request.deviceId,
            model: request.model,
            name: request.deviceName,
            publicKey: request.publicKey,
            pushId: request.pushID,
            signature: request.signature,
        };

        console.log("Request Body:", JSON.stringify(regRequestBody));

        let requestMethod = "POST";

        let headers = {
            Accept: "application/json",
            "Content-Type": "application/json",
        };

        let newRequest: RequestSenderUtil = new RequestSenderUtil();
        let registrationUrl =
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
                if (result.status === 200) {
                    account = {
                        deviceID: request.deviceId,
                        username: discoveryData.username,
                        firstName: discoveryData.firstName,
                        lastName: discoveryData.lastName,
                        tenantDomain: discoveryData.tenantDomain,
                        host: discoveryData.host,
                        basePath: discoveryData.basePath,
                        authenticationEndpoint: discoveryData.authenticationEndpoint,
                        removeDeviceEndpoint: discoveryData.removeDeviceEndpoint,
                        privateKey: keypair.prvKey,
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
        account: AccountsInterface,
    ): Promise<string> {

        console.log("Remove account function");
        let jwt = KJUR.jws.JWS.sign(
            null,
            {
                alg: "RS256",
                did: account.deviceID,
            } as any,
            {
                jti: uuid(),
                sub: account.username + "@" + account.tenantDomain,
                iss: "wso2verify",
                aud: account.host + "/t/" + account.tenantDomain + "/",
                nbf: KJUR.jws.IntDate.get("now"),
                exp: KJUR.jws.IntDate.get("now + 1hour"),
                iat: KJUR.jws.IntDate.get("now"),
                act: "REMOVE",
            },
            account.privateKey
        );

        let body = {
            token: jwt,
        };

        let url = account.host + account.basePath + '/push-auth/devices/' + account.deviceID + '/remove';
        let headers = {
            "Content-Type": "application/json",
        };
        let requestMethod = "POST";

        console.log("Device ID: " + account.deviceID);

        let request: RequestSenderUtil = new RequestSenderUtil();

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

        console.log("Process Discovery data");
        console.log("Reg Request: " + regRequest.did);
        console.log("Reg Request ID: " + JSON.stringify(regRequest.un));

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

            throw new Error("One or more required parameters missing");
        }

        if (regRequest.fn) {
            discoveryData.firstName = regRequest.fn;
        }

        if (regRequest.ln) {
            discoveryData.lastName = regRequest.ln;
        }

        console.log("Discovery data test: " + JSON.stringify(discoveryData));

        return discoveryData;
    }
}
