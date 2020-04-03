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
package org.wso2.carbon.identity.mobile.wso2verify

class BiometricAppConstants {
    companion object {

        const val POST = "POST"
        const val CONTEXT_KEY = "sessionDataKey"
        const val CHALLENGE = "challenge"
        const val BODY = "body"
        const val DOMAIN_NAME = "https://biometricauthenticator.private.wso2.com:9443"
        const val BIOMETRIC_ENDPOINT = "/biometric-auth"
        const val SUCCESSFUL = "SUCCESSFUL"
        const val CLICK_ACTION = "click_action"
    }
}
