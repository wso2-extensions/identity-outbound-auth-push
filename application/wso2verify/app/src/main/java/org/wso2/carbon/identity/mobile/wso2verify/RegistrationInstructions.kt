package org.wso2.carbon.identity.mobile.wso2verify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.content_registration_instructions.*
import org.wso2.carbon.identity.mobile.wso2verify.Model.BiometricAuthProfile
import org.wso2.carbon.identity.mobile.wso2verify.Model.DiscoveryDataDTO


class RegistrationInstructions : AppCompatActivity() {

    internal lateinit var db: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_instructions)
        scanqr.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setBeepEnabled(false)
            scanner.initiateScan()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var profiles: ArrayList<BiometricAuthProfile>
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
            var discoveryData: DiscoveryDataDTO =
                Gson().fromJson(result.contents, DiscoveryDataDTO::class.java)
            db = DatabaseHelper(this)
            profiles = db.getProfile(discoveryData)
            if (profiles.isEmpty()) {
                var regService: DeviecRegistrationService = DeviecRegistrationService()
                regService.sendRegistrationRequest(discoveryData, this)
            } else {
                Toast.makeText(
                    this,
                    "This device is already registered for this account",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(this, "Scanning Was cancelled", Toast.LENGTH_LONG).show()
        }

    }

}
