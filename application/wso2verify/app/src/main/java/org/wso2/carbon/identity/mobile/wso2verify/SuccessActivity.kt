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

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.wso2.carbon.identity.mobile.wso2verify.util.impl.RequestUrlBuilderImpl
import java.io.IOException
import java.util.concurrent.CountDownLatch


/**
 * Activity which is opened upon successful fingerprint authentication at the AuthenticateActivity.
 */
class SuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        val actionbar = supportActionBar
        actionbar?.title = getString(R.string.app_name)
        actionbar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent

        try {
            val sessionDataKey = intent.getStringExtra(BiometricAppConstants.CONTEXT_KEY)
            val challenge = intent.getStringExtra(BiometricAppConstants.CHALLENGE)

            val requestUrlBuilderImpl = RequestUrlBuilderImpl()
            requestUrlBuilderImpl.requestUrlBuilder(sessionDataKey, challenge, BiometricAppConstants.SUCCESSFUL)

        } catch (e: Exception) {
            Log.e("Error ", "Error when trying to initiate the network call.", e)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        val launchNextActivity = Intent(this@SuccessActivity, StartupActivity::class.java)
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(launchNextActivity)
        return true
    }
}

@RequiresApi(Build.VERSION_CODES.CUPCAKE)
 class NetworkTask : AsyncTask<String, Int, Long>() {
    override fun doInBackground(vararg parts: String): Long? {

        //val reqBody =JSONObject().put("Status", parts.last()).toString().toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())

        val CONTENT_TYPE = ("application/x-www-form-urlencoded").toMediaTypeOrNull()
        val requestBody: RequestBody = FormBody.Builder()
            .addEncoded("auth_status", parts.last())
            .build()


        val request: Request = Request.Builder()
            .url(parts.first())
            .addHeader("ContentType", "application/x-www-form-urlencoded")
            .post(requestBody)
            .build()
        var client: OkHttpClient = DeviecRegistrationService().getUnsafeOkHttpClient()
        val countDownLatch = CountDownLatch(1)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                countDownLatch.countDown()
            }
            override fun onResponse(call: Call, response: Response) {
                countDownLatch.countDown()
            }
        })
        countDownLatch.countDown()
        return 0
    }
}
