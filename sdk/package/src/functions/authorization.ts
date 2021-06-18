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
import {RequestSender} from "../utils/requestSender";

import {DateTime} from "../utils/dateTime";
import {KJUR} from "jsrsasign";
import uuid from "uuid-random";
import {AccountsInterface} from "../models";

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
     * @param account Registered account requesting to authenticate
     */
    public static async sendAuthRequest(
        authRequest: AuthRequestInterface,
        response: string,
        account: AccountsInterface
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
                aud: account.host + "/t/" + account.tenantDomain + "/",
                nbf: KJUR.jws.IntDate.get("now"),
                exp: KJUR.jws.IntDate.get("now + 1hour"),
                iat: KJUR.jws.IntDate.get("now"),
                sid: authRequest.sessionDataKey,
                chg: authRequest.challenge,
                res: response,
            },
            account.privateKey
        );

        let headers = {
            Accept: "application/json",
            "Content-Type": "application/json",
        };

        let authRequestBody: any = {
            authResponse: jwt,
        };


        let authUrl = account.host + account.authenticationEndpoint;
        console.log("Request URL: " + authUrl);
        console.log(authRequestBody);

        let request = new RequestSender();
        let result: Promise<any> = request.sendRequest(
            authUrl, "POST", headers, JSON.stringify(authRequestBody));

        authRequest.requestTime = timestamp.getDateTime();

        return result.then((res) => {
            console.log("Response test: " + res);
            let result;
            if (res.status === 202 || res.status === 200 && response == "SUCCESSFUL") {
                authRequest.authenticationStatus = "Accepted";
                result = "OK";
                console.log("Auth is OK and Accepted");
            } else if (res.status === 202 || res.status === 200 && response == "DENIED") {
                authRequest.authenticationStatus = "Denied";
                result = "FAILED";
                console.log("Auth is OK and Denied");
            } else {
                console.log("Auth response has a problem. Check! " + String(res));
            }
            console.log(authRequest.authenticationStatus);

            return JSON.stringify({res: result, data: authRequest});
        });
    }

}
