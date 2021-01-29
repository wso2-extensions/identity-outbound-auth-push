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
import kotlinx.android.synthetic.main.activity_startup.*

/**
 * The startup screen of the application.
 */
class StartupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentAuthenticate = intent
        setContentView(R.layout.activity_startup)
        if (intentAuthenticate.hasExtra(BiometricAppConstants.CHALLENGE)) {
            val authIntent = Intent(this, AuthenticateActivity::class.java)
            authIntent.putExtra(
                BiometricAppConstants.CONTEXT_KEY,
                intentAuthenticate.getStringExtra(BiometricAppConstants.CONTEXT_KEY)
            )
            authIntent.putExtra(
                BiometricAppConstants.CHALLENGE,
                intentAuthenticate.getStringExtra(BiometricAppConstants.CHALLENGE)
            )
            authIntent.putExtra(
                BiometricAppConstants.BODY,
                intentAuthenticate.getStringExtra(BiometricAppConstants.BODY)
            )
            authIntent.putExtra(
                BiometricAppConstants.DEVIECID,
                intentAuthenticate.getStringExtra(BiometricAppConstants.DEVIECID)
            )
            startActivity(authIntent)
        }
        startup_register.setOnClickListener {
            val intent = Intent(this, QRScanActivity::class.java)
            startActivity(intent)
        }
        startup_profiles.setOnClickListener{
            val intent = Intent(this, UserProfilesActivity::class.java)
            startActivity(intent)
        }

    }

}
