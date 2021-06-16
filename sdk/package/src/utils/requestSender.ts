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

import { fetch } from "react-native-ssl-pinning";
import { Alert } from "react-native";

export class RequestSender {
  public constructor() {}

  public async sendRequest(
    url: string,
    requestMethod: any,
    requestHeaders: any,
    body: any
  ): Promise<string> {
    return fetch(url, {
      method: requestMethod,
      disableAllSecurity: true,
      sslPinning: {
        certs: ["wso2carbon"], // TODO: make the certificate name configurable
      },
      headers: requestHeaders,
      body: body,
    })
      .then((response: any) => {
        console.log(`response received ${response.bodyString}`);
        // Alert.alert(
        //   "Request",
        //   "Status: " + response.status + "\nMessage: " + response.bodyString,
        //   [{ text: "OK", onPress: () => console.log("OK Pressed") }],
        //   { cancelable: false }
        // );
        return response.status == "200" ? "OK" : "FAILED";
      })
      .catch((err: any) => {
        console.log(`error: ${err.status}`);
        return "FAILED";
      });
  }
}
