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
package org.wso2.carbon.identity.mobile.wso2verify.util.impl

import android.content.Context
import android.util.Log

import org.wso2.carbon.identity.mobile.wso2verify.BiometricAppConstants
import org.wso2.carbon.identity.mobile.wso2verify.DatabaseHelper
import org.wso2.carbon.identity.mobile.wso2verify.NetworkTask
import org.wso2.carbon.identity.mobile.wso2verify.util.RequestUrlBuilder

class RequestUrlBuilderImpl : RequestUrlBuilder {
    internal lateinit var db: DatabaseHelper
    override fun requestUrlBuilder(sessionDataKey: String?, challenge: String, consent: String?, deviceId: String , context: Context): String {
        db = DatabaseHelper(context)
        val data = db.getProfileData(deviceId)
        val signature = BiometricAuthUtil.signChallenge(data.privateKey, challenge)
        val requestURL = data.authUrl + "?initiator=mobile&sessionDataKey=" +
                sessionDataKey + "&challenge=" + challenge
        Log.d("Request Url of the polling endpoint: ", requestURL)
        NetworkTask().execute(requestURL, signature,deviceId, consent)
        return requestURL
    }
}
