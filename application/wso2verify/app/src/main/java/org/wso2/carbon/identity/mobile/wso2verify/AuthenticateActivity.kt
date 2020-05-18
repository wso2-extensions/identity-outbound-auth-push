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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executors
import android.widget.TextView
import androidx.biometric.BiometricPrompt
import com.google.firebase.iid.FirebaseInstanceId
import android.text.method.ScrollingMovementMethod
import kotlinx.android.synthetic.main.activity_authenticate.*
import okhttp3.OkHttpClient
import org.wso2.carbon.identity.mobile.wso2verify.util.impl.RequestUrlBuilderImpl


/**
 * Activity which authenticates the user with fingerprint.
 */
class AuthenticateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticate)

        val actionbar = supportActionBar
        actionbar?.title = getString(R.string.app_name)
        actionbar?.setDisplayHomeAsUpEnabled(true)

        val intentAuthenticate = intent
        val token = FirebaseInstanceId.getInstance().token
        Log.d("TAG", "Device ID of the android device: $token")

        val messageSessionDataKey =
            intentAuthenticate.getStringExtra(BiometricAppConstants.CONTEXT_KEY)
        val messageChallenge = intentAuthenticate.getStringExtra(BiometricAppConstants.CHALLENGE)
        val notificationBody = intentAuthenticate.getStringExtra(BiometricAppConstants.BODY)
        Log.d("TAG", "session data key at authenticate activity: $messageSessionDataKey")
        Log.d("TAG", "challenge at auth activity: $messageChallenge")

        fun text(): String {
            val textViewDynamic = TextView(this)
            textViewDynamic.text = notificationBody
            Log.d("New text: ", textViewDynamic.text.toString())
            return textViewDynamic.text.toString()

        }

        notification_display.movementMethod = ScrollingMovementMethod()
        notification_display.text = text()

        val executor = Executors.newSingleThreadExecutor()
        val activity: FragmentActivity = this // reference to activity

        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result:
                    BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    if (messageSessionDataKey != null && messageChallenge != null) {
                        success(messageSessionDataKey, messageChallenge)
                    }
                }
            })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_prompt_message))
            .setNegativeButtonText(getString(R.string.close))
            .build()
        findViewById<Button>(R.id.allow_button).setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
        deny_button.setOnClickListener {
            sendAuthDeniedResponse()
            val intent = Intent(this, StartupActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onSupportNavigateUp(): Boolean {

        onBackPressed()
        val intent = Intent(applicationContext, StartupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        return true
    }

    private fun success(sessionDataKey: String?, challenge: String) {

        val intentSuccess = Intent(this, SuccessActivity::class.java)
        intentSuccess.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intentSuccess.putExtra(BiometricAppConstants.CONTEXT_KEY, sessionDataKey)
        intentSuccess.putExtra(BiometricAppConstants.CHALLENGE, challenge)
        startActivity(intentSuccess)
    }

    private fun sendAuthDeniedResponse() {
        try {
            RequestUrlBuilderImpl().requestUrlBuilder(
                intent.getStringExtra(BiometricAppConstants.CONTEXT_KEY),
                intent.getStringExtra(BiometricAppConstants.CHALLENGE),
                BiometricAppConstants.DENIED

            )

        } catch (e: Exception) {
            Log.e("Error ", "Error when trying to initiate the network call.", e)
        }
    }
}

