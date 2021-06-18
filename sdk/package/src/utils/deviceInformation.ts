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

import DeviceInfo from 'react-native-device-info';

/**
 * Util class for handling device information.
 */
export class DeviceInformation {
  private static deviceName: string;
  private static deviceBrand: string;
  private static deviceModel: string;

  public constructor() {

    console.log("Device info initialized");

    // TODO: Is there a requirement to check if the device name has changed?

    // Adding device name
    if (DeviceInformation.deviceName == null) {
      console.log("Constructor called to add details.");
      DeviceInfo.getDeviceName()
        .then((deviceName: string) => {
          console.log('Device Name: ' + deviceName);
          DeviceInformation.deviceName = deviceName;
        })
        .catch((err: string) => {
          console.log('Get device name: ' + err);
        })
    } else {
      console.log("Device info already added.");
    }

    // Adding device brand
    if (DeviceInformation.deviceBrand == null) {
      console.log("Adding device model");
      DeviceInformation.deviceBrand = DeviceInfo.getBrand();
    } else {
      console.log("Model already added.")
    }

    // Adding device model
    if (DeviceInformation.deviceModel == null) {
      console.log("Adding device model");
      DeviceInformation.deviceModel = DeviceInfo.getModel();
    }

  }

  /**
   * Returns the name of the device.
   *
   * @returns deviceName - Name of the device
   */
  public static getDeviceName():string {

    return this.deviceName;
  }

  /**
   * Returns the model name of the device.
   *
   * @returns deviceModel - Model of the device
   */
  public static getDeviceModel(): string {

    return DeviceInformation.deviceModel;
  }

  /**
   * Returns the brand name of the device.
   *
   * @returns deviceBrand - Brand name of the device
   */
  public static getDeviceBrand(): string {

    return DeviceInformation.deviceBrand;
  }

}



