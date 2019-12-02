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
import kotlinx.android.synthetic.main.activity_authenticate.*
import android.text.method.ScrollingMovementMethod


/**
 * Activity which authenticates the user with fingerprint.
 */
class AuthenticateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticate)

        val actionbar = supportActionBar
        actionbar?.title=getString(R.string.app_name)
        actionbar?.setDisplayHomeAsUpEnabled(true)

        val intentAuthenticate = intent
        val token = FirebaseInstanceId.getInstance().token
        Log.d("TAG", "Device ID of the android device: $token")

            val messageSessionDataKey = intentAuthenticate.getStringExtra(BiometricAppConstants.CONTEXT_KEY)
            val messageChallenge = intentAuthenticate.getStringExtra(BiometricAppConstants.CHALLENGE)
            val notificationBody=intentAuthenticate.getStringExtra(BiometricAppConstants.BODY)
            Log.d("TAG","session data key at authenticate activity: $messageSessionDataKey")
            Log.d("TAG","challenge at auth activity: $messageChallenge")

            fun text():String{
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

                    override fun onAuthenticationSucceeded( result:
                    BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        if (messageSessionDataKey != null && messageChallenge != null) {
                            success(messageSessionDataKey, messageChallenge)
                        }
                    }
                })
            val promptInfo = BiometricPrompt.
                PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_prompt_message))
                .setNegativeButtonText(getString(R.string.close))
                .build()
            findViewById<Button>(R.id.allow_button).setOnClickListener {
                biometricPrompt.authenticate(promptInfo)
            }
            findViewById<Button>(R.id.deny_button).setOnClickListener {
                //todo Handle Deny Button-DOCS
            }
    }

    override fun onSupportNavigateUp(): Boolean {

        onBackPressed()
        val intent = Intent(applicationContext, MainActivity::class.java)
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
}
//todo handle if fingerprint is changed by user.-DOCS
