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

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.net.URL
import android.os.AsyncTask
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import android.content.Intent
import org.wso2.carbon.identity.mobile.wso2verify.util.impl.RequestUrlBuilderImpl
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLHandshakeException

/**
 * Activity which is opened upon successful fingerprint authentication at the AuthenticateActivity.
 */
class SuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        val actionbar = supportActionBar
        actionbar?.title=getString(R.string.app_name)
        actionbar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent

        try {
            val sessionDataKey = intent.getStringExtra(BiometricAppConstants.CONTEXT_KEY)
            val challenge = intent.getStringExtra(BiometricAppConstants.CHALLENGE)

            val requestUrlBuilderImpl = RequestUrlBuilderImpl()
            requestUrlBuilderImpl.requestUrlBuilder(sessionDataKey,challenge)

        } catch (e: Exception) {
            Log.e("Error ", "Error when trying to initiate the network call.",e)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        val launchNextActivity = Intent(this@SuccessActivity, MainActivity::class.java)
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(launchNextActivity)
        return true
    }
}

@RequiresApi(Build.VERSION_CODES.CUPCAKE)
public class NetworkTask : AsyncTask<String, Int, Long>() {
    override fun doInBackground(vararg parts: String): Long? {

        val requestURL = parts.first()
        val queryString = parts.last()

        val connection: HttpsURLConnection = URL(requestURL).openConnection() as HttpsURLConnection
        //todo use a library instead of low level coding when initiating the network connection.-postponed.
        try {
            connection.requestMethod = BiometricAppConstants.POST
            connection.doOutput = true

            val outputStream: OutputStream = connection.outputStream
            val outputWriter = OutputStreamWriter(outputStream)
            outputWriter.write(queryString)
            outputWriter.flush()
            } catch (e: SSLHandshakeException){
                Log.e("Error","SSL handshake error occurred", e)
            }

        val inputStream = BufferedReader(InputStreamReader
            (connection.inputStream)).use {
            val response = StringBuffer()
            var inputLine = it.readLine()
            while (inputLine != null) {
                response.append(inputLine)
                inputLine = it.readLine()
            }
            it.close()
            println("Response: $response")
        }
        connection.disconnect()
        return 0
    }
}
