package org.wso2.carbon.identity.mobile.wso2verify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.content_registration_instructions.*
import org.wso2.carbon.identity.mobile.wso2verify.Model.BiometricAuthProfile
import org.wso2.carbon.identity.mobile.wso2verify.Model.DiscoveryDataDTO


class QRScanActivity : AppCompatActivity() {
    private var progressStatus = 0
    internal lateinit var db: DatabaseHelper
    private var handler: Handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_instructions)
        progressBar2.visibility = View.GONE
        scanqr.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setBeepEnabled(false)
            scanner.initiateScan()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        progressBar2.visibility = View.VISIBLE
        var profiles: ArrayList<BiometricAuthProfile>
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            var discoveryData: DiscoveryDataDTO =
                Gson().fromJson(result.contents, DiscoveryDataDTO::class.java)
            db = DatabaseHelper(this)
            profiles = db.getProfilesFromQrData(discoveryData)

//            if (profiles.isEmpty()) {
                Thread(Runnable {
                    while(progressStatus<100){
                        progressStatus++
                        SystemClock.sleep(100)
                        handler.post(Runnable {
                            progressBar2.progress = progressStatus
                        })
                    }
                }).start()
                var regService: DeviecRegistrationService = DeviecRegistrationService()
                regService.sendRegistrationRequest(discoveryData, this)
                progressBar2.visibility = View.VISIBLE
//            } else {
//                Toast.makeText(
//                    this,
//                    "This device is already registered for this account",
//                    Toast.LENGTH_LONG
//                ).show()
//            progressBar2.visibility = View.GONE
//            }

        } else {
            Toast.makeText(this, "Scanning Was cancelled", Toast.LENGTH_LONG).show()
            progressBar2.visibility = View.GONE
        }

    }

}
