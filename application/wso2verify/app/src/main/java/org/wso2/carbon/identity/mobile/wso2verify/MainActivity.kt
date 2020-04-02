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

/**
 * Main Activity which default opens when the app is clicked.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionbar = supportActionBar
        actionbar?.title= getString(R.string.app_name)

        val intentMain = intent

        if (intentMain.hasExtra(BiometricAppConstants.CONTEXT_KEY)) {
            val authenticateIntent = Intent(this, AuthenticateActivity::class.java)

            authenticateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            authenticateIntent.putExtra(
                BiometricAppConstants.CONTEXT_KEY,intentMain.getStringExtra(
                    BiometricAppConstants.CONTEXT_KEY
                ))
            authenticateIntent.putExtra(
                BiometricAppConstants.CHALLENGE,intentMain.getStringExtra(
                    BiometricAppConstants.CHALLENGE
                ))
            authenticateIntent.putExtra(
                BiometricAppConstants.BODY,intentMain.getStringExtra(
                    BiometricAppConstants.BODY
                ))
            startActivity(authenticateIntent)
            finish()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

// handle enabling push notifs in IOS.
//force stop app-how notifications are handled?
//try pull option if push notification delivery fails.
//how to handle if push notifs are disabled in android.

