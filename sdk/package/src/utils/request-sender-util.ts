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

import {fetch} from "react-native-ssl-pinning";

/**
 * Util class for handling http requests.
 */
export class RequestSenderUtil {

    /**
     * Sends request to required endpoint.
     *
     * @param url Endpoint the request is sent to
     * @param requestMethod Request method
     * @param requestHeaders Request headers
     * @param body Request body
     */
    public async sendRequest(
        url: string,
        requestMethod: any,
        requestHeaders: any,
        body: any
    ): Promise<any> {

        return fetch(url, {
            method: requestMethod,
            disableAllSecurity: true,
            sslPinning: {
                certs: ["wso2carbon"]
            },
            headers: requestHeaders,
            body: body
        })
            .then((response: any) => {

                return response;
            })
            .catch((err: any) => {
                console.log(`error: ${err.status}`);

                return "FAILED";
            });
    }
}
