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

import {AuthRequestInterface} from "../models/authRequest";
import {Crypto} from "../utils/crypto";
import {RequestSender} from "../utils/requestSender";

import AsyncStorage from "@react-native-async-storage/async-storage";
import {DateTime} from "../utils/dateTime";
import {KJUR} from "jsrsasign";
import uuid from "uuid-random";

let privateKey: string;

const getData = async () => {
    try {
        const value = await AsyncStorage.getItem("privateKey");
        if (value !== null) {
            // value previously stored
            privateKey = value;
        }
    } catch (e) {
        // error reading value
        console.log("No private key available");
    }
};

getData();

export class Authorization {

    constructor() {
    }

    /**
     * Process the request as an organized object.
     *
     * @param request JSON object of the request
     */
    public static processAuthRequest(
        request: any
    ): AuthRequestInterface {
        let authRequest: AuthRequestInterface;

        if (
            request.data.deviceId &&
            request.data.challenge &&
            request.data.sessionDataKey
        ) {
            authRequest = {
                deviceId: request.data.deviceId,
                challenge: request.data.challenge,
                sessionDataKey: request.data.sessionDataKey,
                authUrl: "https://192.168.1.112:9443/push-auth/authenticate",
                privateKey: privateKey,
                connectionCode: (
                    request.data.sessionDataKey.substring(0, 4) +
                    " - " +
                    request.data.sessionDataKey.substring(4, 8)
                ).toUpperCase(),
            };
        } else {
            throw new Error("One or more required parameters missing");
        }

        if (request.data.displayName) {
            authRequest.displayName = request.data.displayName;
        }

        if (request.data.username) {
            authRequest.username = request.data.username;
        }

        if (request.data.organization) {
            authRequest.organization = request.data.organization;
        }

        if (request.data.applicationName) {
            authRequest.applicationName = request.data.applicationName;
        }

        if (request.data.applicationUrl) {
            authRequest.applicationUrl = request.data.applicationUrl;
        }

        if (request.data.deviceName) {
            authRequest.deviceName = request.data.deviceName;
        }

        if (request.data.browserName) {
            authRequest.browserName = request.data.browserName;
        }

        if (request.data.ipAddress) {
            authRequest.ipAddress = request.data.ipAddress;
        }

        if (request.data.location) {
            authRequest.location = request.data.location;
        }

        return authRequest;
    }

    /**
     * Returns the timestamp which the request will expire at.
     *
     * @param requestInitTime the time the request was initialized in the IS
     */
    public getRequestExpiryTime(requestInitTime: any) {
        // Add code here
    }

    /**
     * Send the request to the IS to allow or deny authorization.
     *
     * @param authRequest Object for the authentication request
     * @param response Authorisation response given by the user
     */
    public static async sendAuthRequest(
        authRequest: AuthRequestInterface,
        response: string
    ): Promise<any> {
        console.log("challenge: " + authRequest.challenge);

        let timestamp = new DateTime();

        let jwt = KJUR.jws.JWS.sign(
            null,
            {
                alg: "RS256",
                did: authRequest.deviceId,
            } as any,
            {
                jti: uuid(),
                sub: authRequest.username + "@" + authRequest.organization,
                iss: "wso2verify",
                aud: "https://localhost:9443/t/" + authRequest.organization + "/",
                nbf: KJUR.jws.IntDate.get("now"),
                exp: KJUR.jws.IntDate.get("now + 1hour"),
                iat: KJUR.jws.IntDate.get("now"),
                sid: authRequest.sessionDataKey,
                chg: authRequest.challenge,
                res: response,
            },
            authRequest.privateKey
        );

        let headers = {
            Accept: "application/json",
            "Content-Type": "application/json",
        };

        let authRequestBody: any = {
            jwt: jwt,
        };


        console.log("Request URL: " + authRequest.authUrl);
        console.log(authRequestBody);

        let request = new RequestSender();
        let result: Promise<string> = request.sendRequest(
            authRequest.authUrl,
            // "https://enx6srhygagwwxs.m.pipedream.net",
            "POST",
            headers,
            authRequestBody
        );

        authRequest.requestTime = timestamp.getDateTime();

        return result.then((result) => {
            console.log("Response test: " + result);
            if (result == "OK" && response == "SUCCESSFUL") {
                authRequest.authenticationStatus = "Accepted";
                console.log("Auth is OK and Accepted");
            } else if (result == "OK" && response == "DENIED") {
                authRequest.authenticationStatus = "Denied";
                console.log("Auth is OK and Denied");
            } else {
                console.log("Auth response has a problem. Check! " + String(result));
            }
            console.log(authRequest.authenticationStatus);
            return JSON.stringify({res: result, data: authRequest});
        });
    }

    /**
     * Checks if a value is null.
     *
     * @param value Value to be checked for null
     */
    private isNotNull(value: any): boolean {
        if (value != null) {
            return true;
        } else {
            return false;
        }
    }

    public static updateSavedData() {
        getData();
    }
}
