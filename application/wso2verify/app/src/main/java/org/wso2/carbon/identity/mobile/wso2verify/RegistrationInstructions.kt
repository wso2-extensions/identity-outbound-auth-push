package org.wso2.carbon.identity.mobile.wso2verify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator

import kotlinx.android.synthetic.main.activity_registration_instructions.*
import kotlinx.android.synthetic.main.content_registration_instructions.*
import org.wso2.carbon.identity.mobile.wso2verify.Model.DiscoveryDataDTO

class RegistrationInstructions : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_instructions)
        scanqr.setOnClickListener{
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setBeepEnabled(false)
            scanner.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
            var discoveryData: DiscoveryDataDTO = Gson().fromJson(result.contents, DiscoveryDataDTO::class.java)
        }
        else{
            Toast.makeText(this, "Scanning Was cancelled", Toast.LENGTH_LONG).show()

        }

    }

}
