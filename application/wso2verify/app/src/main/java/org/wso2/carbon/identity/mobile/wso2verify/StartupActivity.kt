package org.wso2.carbon.identity.mobile.wso2verify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
            startActivity(authIntent)
        }
        startup_register.setOnClickListener {
            val intent = Intent(this, QRScanActivity::class.java)
            startActivity(intent)
        }


    }

}
