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

