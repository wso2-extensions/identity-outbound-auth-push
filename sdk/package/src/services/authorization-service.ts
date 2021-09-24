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
import {AccountsInterface, AuthRequestInterface} from "../models";
import {DateTimeUtil, RequestSenderUtil} from "../utils";

export class AuthorizationService {

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
                organization: request.data.organization,
                username: request.data.username,
                deviceId: request.data.deviceId,
                challenge: request.data.challenge,
                sessionDataKey: request.data.sessionDataKey,
                connectionCode: (
                    request.data.sessionDataKey.substring(0, 4) +
                    " - " +
                    request.data.sessionDataKey.substring(4, 8)
                ).toUpperCase()
            };
        } else {

            throw new Error("One or more required parameters (deviceId, challenge, sessionDataKey) was not found.");
        }

        if (request.data.displayName) {
            authRequest.displayName = request.data.displayName;
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

        if (request.data.metadata) {
            authRequest.metadata = request.data.metadata;
        }

        return authRequest;
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

        const timestamp = new DateTimeUtil();

        const jwt = KJUR.jws.JWS.sign(
            null,
            {
                alg: "RS256",
                did: authRequest.deviceId
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
                mta: authRequest.metadata,
                res: response
            },
            account.privateKey
        );

        const headers = {
            Accept: "application/json",
            "Content-Type": "application/json"
        };

        const authRequestBody: any = {
            authResponse: jwt
        };


        const authUrl = account.host + account.authenticationEndpoint;

        const request = new RequestSenderUtil();
        const result: Promise<any> = request.sendRequest(
            authUrl, "POST", headers, JSON.stringify(authRequestBody));

        authRequest.requestTime = timestamp.getDateTime();

        return result.then((res) => {
            let result;
            if (res.status === 202 && response == "SUCCESSFUL") {
                authRequest.authenticationStatus = "Accepted";
                result = "OK";
            } else if (res.status === 202 && response == "DENIED") {
                authRequest.authenticationStatus = "Denied";
                result = "OK";
            } else {
                console.error("Auth response has a problem. Check! " + String(res));
            }

            return JSON.stringify({res: result, data: authRequest});
        });
    }

}
