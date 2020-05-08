package org.wso2.carbon.identity.mobile.wso2verify

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.content_registration_failed.*

class RegistrationFailedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_failed)
        try_again.setOnClickListener{
            val intent = Intent(this, RegistrationInstructions::class.java)
            startActivity(intent)
        }

    }

}
