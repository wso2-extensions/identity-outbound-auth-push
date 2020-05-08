package org.wso2.carbon.identity.mobile.wso2verify

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_registration_success.*
import kotlinx.android.synthetic.main.content_registration_success.*

class RegistrationSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_success)
        success_complete.setOnClickListener{
            val intent = Intent(this, StartupActivity::class.java)
            startActivity(intent)
        }
    }

}
