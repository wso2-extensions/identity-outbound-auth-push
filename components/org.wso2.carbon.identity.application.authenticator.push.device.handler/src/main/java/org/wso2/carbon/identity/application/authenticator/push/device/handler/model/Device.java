/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import java.util.Date;

/**
 * This class contains the attributes and operations of a device.
 */
public class Device implements Serializable {

    private String deviceId;
    private String userId;
    private String deviceName;
    private String deviceModel;
    private String pushId;
    private String publicKey;
    private Date registrationTime;
    private Date lastUsedTime;

    public Device() {

    }

    public Device(String deviceId, String userId, String deviceName, String deviceModel,
                  String pushId, String publicKey, Date registrationTime, Date lastUsedTime) {

        this.deviceId = deviceId;
        this.userId = userId;
        this.deviceName = deviceName;
        this.deviceModel = deviceModel;
        this.pushId = pushId;
        this.publicKey = publicKey;
        this.registrationTime = registrationTime;
        this.lastUsedTime = lastUsedTime;
    }

    public Device(String deviceId, String deviceName, String deviceModel,
                  String pushId, String publicKey, Date registrationTime, Date lastUsedTime) {

        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceModel = deviceModel;
        this.pushId = pushId;
        this.publicKey = publicKey;
        this.registrationTime = registrationTime;
        this.lastUsedTime = lastUsedTime;
    }

    public Device(String deviceName, String deviceModel, String pushId, String publicKey) {

        this.deviceName = deviceName;
        this.deviceModel = deviceModel;
        this.pushId = pushId;
        this.publicKey = publicKey;
    }

    public Device(String deviceId, String userId, String deviceName, String deviceModel, String pushId, String publicKey) {

        this.deviceId = deviceId;
        this.userId = userId;
        this.deviceName = deviceName;
        this.deviceModel = deviceModel;
        this.pushId = pushId;
        this.publicKey = publicKey;
    }

    public String getDeviceId() {

        return deviceId;
    }

    public void setDeviceId(String deviceId) {

        this.deviceId = deviceId;
    }

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {

        this.userId = userId;
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

    public Date getRegistrationTime() {

        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {

        this.registrationTime = registrationTime;
    }

    public Date getLastUsedTime() {

        return lastUsedTime;
    }

    public void setLastUsedTime(Date lastUsedTime) {

        this.lastUsedTime = lastUsedTime;
    }
}
