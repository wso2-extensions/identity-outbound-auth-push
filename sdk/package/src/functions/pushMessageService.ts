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

import AsyncStorage from "@react-native-async-storage/async-storage";

/**
 * Class for handling push message device tokens.
 */
export class PushMessageService {

    /**
     * Save the push message device token in React Native Async Storage.
     *
     * @param token Push message device token
     */
    public static async savePushToken(token: string) {
        if (token) {
            // user has a device token
            await AsyncStorage.setItem("fcmToken", token);
            console.log("Push Token" + token);

            // TODO: change the name of the async storage item name to be more generic
        } else {
            throw new Error("Push token not available");
        }
    }

    /**
     * Update push message device token if a change is identified.
     *
     * @param token New push message device token
     * @param saveToken Boolean true if saving token internally
     */
    public static async updatePushToken(token: string, saveToken?: boolean) {
        if (token) {
            //TODO: Add functionality to send request to update Push token in server

            // Update token in async storage
            if (saveToken) {
                this.savePushToken(token);
                console.log("Push token updated");
            }
        } else {
            throw new Error("Push token not available");
        }
    }

    /**
     * Get the push token
     *
     * @returns promise of push token
     */
    public static async getPushToken(): Promise<any> {
        await AsyncStorage.getItem("privateKey")
            .then((token: any) => {
                return token;
            })
            .catch((err: any) => {
                throw new Error("NullPushTokenError: " + err);
            });
    }
}

// TODO: Test the functionality of getPushToken()
