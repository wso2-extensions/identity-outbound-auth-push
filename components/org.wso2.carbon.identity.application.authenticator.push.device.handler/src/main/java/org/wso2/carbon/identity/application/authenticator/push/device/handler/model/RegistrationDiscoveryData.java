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
 * This class contains the attributes sent to generate the qr code .
 */
public class RegistrationDiscoveryData implements Serializable {

    private String deviceId;
    private String username;
    private String firstName;
    private String lastName;
    private String tenantDomain;
    private String host;
    private String basePath;
    private String registrationEndpoint;
    private String removeDeviceEndpoint;
    private String authenticationEndpoint;
    private String challenge;

    public RegistrationDiscoveryData(String deviceId, String username, String firstName, String lastName,
                                     String tenantDomain, String host, String basePath, String registrationEndpoint,
                                     String removeDeviceEndpoint, String authenticationEndpoint, String challenge) {

        this.deviceId = deviceId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.tenantDomain = tenantDomain;
        this.host = host;
        this.basePath = basePath;
        this.registrationEndpoint = registrationEndpoint;
        this.removeDeviceEndpoint = removeDeviceEndpoint;
        this.authenticationEndpoint = authenticationEndpoint;
        this.challenge = challenge;
    }

    public RegistrationDiscoveryData() {

    }

    public String getDeviceId() {

        return deviceId;
    }

    public void setDeviceId(String deviceId) {

        this.deviceId = deviceId;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getTenantDomain() {

        return tenantDomain;
    }

    public void setTenantDomain(String tenantDomain) {

        this.tenantDomain = tenantDomain;
    }

    public String getChallenge() {

        return challenge;
    }

    public void setChallenge(String challenge) {

        this.challenge = challenge;
    }

    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }

    public String getLastName() {

        return lastName;
    }

    public void setLastName(String lastName) {

        this.lastName = lastName;
    }

    public String getHost() {

        return host;
    }

    public void setHost(String host) {

        this.host = host;
    }

    public String getBasePath() {

        return basePath;
    }

    public void setBasePath(String basePath) {

        this.basePath = basePath;
    }

    public String getRegistrationEndpoint() {

        return registrationEndpoint;
    }

    public void setRegistrationEndpoint(String registrationEndpoint) {

        this.registrationEndpoint = registrationEndpoint;
    }

    public String getRemoveDeviceEndpoint() {

        return removeDeviceEndpoint;
    }

    public void setRemoveDeviceEndpoint(String removeDeviceEndpoint) {

        this.removeDeviceEndpoint = removeDeviceEndpoint;
    }

    public String getAuthenticationEndpoint() {

        return authenticationEndpoint;
    }

    public void setAuthenticationEndpoint(String authenticationEndpoint) {

        this.authenticationEndpoint = authenticationEndpoint;
    }
}
