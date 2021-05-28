/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.application.authenticator.push.device.handler.model;

import java.io.Serializable;

/**
 * This class contains the attributes and operations of the request received from the device .
 */
public class RegistrationRequest implements Serializable {

    private static final long serialVersionUID = -2504314979770909509L;
    private String deviceId;
    private String deviceName;
    private String deviceModel;
    private String pushId;
    private String publicKey;
    private String signature;

    public RegistrationRequest(String deviceId, String deviceName, String deviceModel, String pushId,
                               String publicKey, String signature) {

        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceModel = deviceModel;
        this.pushId = pushId;
        this.publicKey = publicKey;
        this.signature = signature;
    }

    public RegistrationRequest() {

    }

    public String getDeviceId() {

        return deviceId;
    }

    public void setDeviceId(String deviceId) {

        this.deviceId = deviceId;
    }

    public String getDeviceName() {

        return deviceName;
    }

    public void setDeviceName(String deviceName) {

        this.deviceName = deviceName;
    }

    public String getDeviceModel() {

        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {

        this.deviceModel = deviceModel;
    }

    public String getPushId() {

        return pushId;
    }

    public void setPushId(String pushId) {

        this.pushId = pushId;
    }

    public String getPublicKey() {

        return publicKey;
    }

    public void setPublicKey(String publicKey) {

        this.publicKey = publicKey;
    }

    public String getSignature() {

        return signature;
    }

    public void setSignature(String signature) {

        this.signature = signature;
    }
}
