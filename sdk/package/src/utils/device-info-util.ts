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

import DeviceInfo from "react-native-device-info";

/**
 * Util class for handling device information.
 */
export class DeviceInfoUtil {
    private static deviceName: string;
    private static deviceBrand: string;
    private static deviceModel: string;

    public constructor() {

        if (DeviceInfoUtil.deviceName == null) {
            DeviceInfo.getDeviceName()
                .then((deviceName: string) => {
                    DeviceInfoUtil.deviceName = deviceName;
                })
                .catch((err: string) => {
                    console.log("Error occurred when trying to get device name: " + err);
                })
        } else {
            console.log("Device info already added.");
        }

        // Adding device brand
        if (DeviceInfoUtil.deviceBrand == null) {
            DeviceInfoUtil.deviceBrand = DeviceInfo.getBrand();
        } else {
            console.log("Model already added.")
        }

        // Adding device model
        if (DeviceInfoUtil.deviceModel == null) {
            DeviceInfoUtil.deviceModel = DeviceInfo.getModel();
        }

    }

    /**
     * Returns the name of the device.
     *
     * @returns deviceName - Name of the device
     */
    public static getDeviceName(): string {

        return this.deviceName;
    }

    /**
     * Returns the model name of the device.
     *
     * @returns deviceModel - Model of the device
     */
    public static getDeviceModel(): string {

        return DeviceInfoUtil.deviceModel;
    }

    /**
     * Returns the brand name of the device.
     *
     * @returns deviceBrand - Brand name of the device
     */
    public static getDeviceBrand(): string {

        return DeviceInfoUtil.deviceBrand;
    }

}



