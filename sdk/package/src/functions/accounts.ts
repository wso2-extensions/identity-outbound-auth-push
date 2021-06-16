/**
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import {RegistrationRequestInterface} from "../models/registrationRequest";
import {AccountsInterface} from "../models/accounts";
import {DiscoveryDataInterface} from "../models/discoveryData";
import {DeviceInformation} from "../utils/deviceInformation";
import {Crypto} from "../utils/crypto";
import uuid from "uuid-random";
import {RequestSender} from "../utils/requestSender";
import AsyncStorage from "@react-native-async-storage/async-storage";
import {Authorization} from "./authorization";
import {KJUR} from "jsrsasign";

let asyncPriv: string;
let asyncId: string | null;

const getData = async () => {
    try {
        const value = await AsyncStorage.getItem("privateKey");
        if (value !== null) {
            // value previously stored
            asyncPriv = value;
        }
        const value2 = await AsyncStorage.getItem("deviceId");
        if (value !== null) {
            // value previously stored
            asyncId = value2;
        }
    } catch (e) {
        // error reading value
        console.log("No private key available");
    }
};

getData();

/**
 * Class for all the functionality related to accounts.
 */
export class Accounts {
    private static accountsList: Array<AccountsInterface> = [];

    constructor() {
        if (Accounts.accountsList == null) {
            Accounts.accountsList = [];
        }
    }

    storeData = async (privKey: string) => {
        try {
            await AsyncStorage.setItem("privateKey", privKey);
        } catch (e) {
            console.log("Async storage error: " + e);
        }
    };

    storeData1 = async (deviceId: string) => {
        try {
            await AsyncStorage.setItem("deviceId", deviceId);
        } catch (e) {
            console.log("Async storage error: " + e);
        }
    };

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

        let keypair: any = Crypto.generateKeypair();
        let signatureString = regRequest.chg + "." + fcmToken;
        console.log("Keypair:", keypair);
        let signedChallenge: string = Crypto.signChallenge(
            keypair.prvKey,
            signatureString
        );

        // Store data for later use
        this.storeData(keypair.prvKey);
        this.storeData1(discoveryData.deviceId);
        getData();
        Authorization.updateSavedData();

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

        request.deviceName = DeviceInformation.getDeviceName();
        request.model = DeviceInformation.getDeviceModel();

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

        let newRequest: RequestSender = new RequestSender();
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
                if (result == "OK") {
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
                } else {
                    account = {deviceID: request.deviceId};
                }

                return JSON.stringify({res: result, data: account});
            });
    }

    /**
     * Revoke the enrollment of the device from the Identity Server
     *
     * @param accountID Unique ID to identify the account
     */
    public async removeAccount(
        accountID: string,
    ): Promise<string> {
        console.log("Remove account function");
        let challenge = uuid();
        let signature = Crypto.signChallenge(asyncPriv, challenge);

        console.log("Remove Account Challenge: " + challenge);
        console.log("Remove Account Sig: " + signature);

        let jwt = KJUR.jws.JWS.sign(
            null,
            {alg: "RS256"},
            {
                jti: uuid(),
                sub: "username@tenant.domain",
                iss: "wso2verify",
                aud: "https://localhost:9443/t/" + "organization" + "/",
                nbf: KJUR.jws.IntDate.get("now"),
                exp: KJUR.jws.IntDate.get("now + 1hour"),
                iat: KJUR.jws.IntDate.get("now"),
                did: asyncId,
                act: "REMOVE",
            },
            asyncPriv
        );

        let body = {
            token: jwt,
        };

        let url =
            "https://192.168.1.112:9443/t/carbon.super/api/users/v1/me/push-auth/devices/" +
            asyncId +
            "/remove";
        let headers = {
            "Content-Type": "application/json",
        };
        let requestMethod = "POST";

        console.log("Device ID: " + asyncId);

        let request: RequestSender = new RequestSender();
        return request.sendRequest(url, requestMethod, headers, body);
    }

    /**
     * Get an account from saved accounts.
     *
     * @param accountsList List of accounts
     * @param accountID Unique ID to identify the account
     */
    public static getAccount(accountsList: any, accountID: string): any {
        accountsList.filter((account: any) => {
            console.log(
                "Correct get account: " + JSON.stringify(account.deviceID === accountID)
            );
            return account.deviceID === accountID;
        });
    }

    /**
     * Returns the list of saved accounts.
     */
    getAccounts(): Array<AccountsInterface> {
        return Accounts.accountsList;
    }

    /*
     *  Internal functions
     */

    /**
     * Organizes the data into a complex object.
     *
     * @param regRequest JSON string from the QR code
     */
    private processDiscoveryData(regRequest: any): DiscoveryDataInterface {
        // TODO: Change structure once the API on the IS is corrected

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
                deviceId: regRequest.did,
                username: regRequest.un,
                host: regRequest.hst,
                basePath: regRequest.bp,
                registrationEndpoint: regRequest.re,
                authenticationEndpoint: regRequest.ae,
                removeDeviceEndpoint: regRequest.rde,
                challenge: regRequest.chg,
            };
        } else {
            throw new Error("One or more required parameters missing");
        }

        if (regRequest.td) {
            discoveryData.tenantDomain = regRequest.td;
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