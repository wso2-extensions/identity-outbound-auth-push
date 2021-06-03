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
 *
 */

package org.wso2.carbon.identity.application.authenticator.push.dto;

/**
 * DTO class for push notification data.
 */
public class PushNotificationDataDTO {
    private String deviceId;
    private String pushId;
    private String message;
    private String randomChallenge;
    private String sessionDataKey;
    private String username;
    private String fullName;
    private String organization;
    private String serviceProviderName;
    private String hostname;
    private String userOS;
    private String userBrowser;

    public String getDeviceId() {

        return deviceId;
    }

    public void setDeviceId(String deviceId) {

        this.deviceId = deviceId;
    }

    public String getPushId() {

        return pushId;
    }

    public void setPushId(String pushId) {

        this.pushId = pushId;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public String getRandomChallenge() {

        return randomChallenge;
    }

    public void setRandomChallenge(String randomChallenge) {

        this.randomChallenge = randomChallenge;
    }

    public String getSessionDataKey() {

        return sessionDataKey;
    }

    public void setSessionDataKey(String sessionDataKey) {

        this.sessionDataKey = sessionDataKey;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getFullName() {

        return fullName;
    }

    public void setFullName(String fullName) {

        this.fullName = fullName;
    }

    public String getOrganization() {

        return organization;
    }

    public void setOrganization(String organization) {

        this.organization = organization;
    }

    public String getServiceProviderName() {

        return serviceProviderName;
    }

    public void setServiceProviderName(String serviceProviderName) {

        this.serviceProviderName = serviceProviderName;
    }

    public String getHostname() {

        return hostname;
    }

    public void setHostname(String hostname) {

        this.hostname = hostname;
    }

    public String getUserOS() {

        return userOS;
    }

    public void setUserOS(String userOS) {

        this.userOS = userOS;
    }

    public String getUserBrowser() {

        return userBrowser;
    }

    public void setUserBrowser(String userBrowser) {

        this.userBrowser = userBrowser;
    }
}
