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
package org.wso2.carbon.identity.mobile.wso2verify.Model


class BiometricAuthProfile {
    var deviceId: String? = null
        get() = field
        set(value) {
            field = value
        }
    var username: String? = null
        get() = field
        set(value) {
            field = value
        }
    var tenantDomain: String? = null
        get() = field
        set(value) {
            field = value
        }
    var userStore: String? = null
        get() = field
        set(value) {
            field = value
        }
    var authUrl: String = ""
        get() = field
        set(value) {
            field = value
        }
    var privateKey: String = ""
        get() = field
        set(value) {
            field = value
        }

    constructor(
        deviceId: String?,
        username: String?,
        tenantDomain: String?,
        userStore: String?,
        authUrl: String,
        privateKey: String
    ) {
        this.deviceId = deviceId
        this.username = username
        this.tenantDomain = tenantDomain
        this.userStore = userStore
        this.authUrl = authUrl
        this.privateKey = privateKey
    }

    constructor()

}