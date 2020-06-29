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

class RegistrationRequestDTO {
    var id: String? = null
        get() = field
        set(value) {
            field = value
        }
    var name: String? = null
        get() = field
        set(value) {
            field = value
        }
    var model: String? = null
        get() = field
        set(value) {
            field = value
        }
    var pushId: String? = null
        get() = field
        set(value) {
            field = value
        }
    var publickey: String? = null
        get() = field
        set(value) {
            field = value
        }
    var signature: String? = null
        get() = field
        set(value) {
            field = value
        }

    constructor(
        id: String?,
        name: String?,
        model: String?,
        pushId: String?,
        publickey: String?,
        signature: String?
    ) {
        this.id = id
        this.name = name
        this.model = model
        this.pushId = pushId
        this.publickey = publickey
        this.signature = signature
    }

    constructor()


}