package org.wso2.carbon.identity.mobile.wso2verify

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_user_profiles.*
import kotlinx.android.synthetic.main.remove_prof_dialog.view.*
import okhttp3.*
import org.wso2.carbon.identity.mobile.wso2verify.Adapters.UserProfileAdapter
import org.wso2.carbon.identity.mobile.wso2verify.Model.BiometricAuthProfile
import org.wso2.carbon.identity.mobile.wso2verify.util.impl.BiometricAuthUtil
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.ArrayList

class UserProfilesActivity : AppCompatActivity(), UserProfileAdapter.OnClickListener {
    internal lateinit var db: DatabaseHelper
    private lateinit var profiles: ArrayList<BiometricAuthProfile>
    override fun onCreate(savedInstanceState: Bundle?) {
        db = DatabaseHelper(this)
        profiles = db.listAllProfiles()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profiles)
        profiles_recycler.adapter = UserProfileAdapter(profiles, this)
        profiles_recycler.layoutManager = LinearLayoutManager(this)
    }

    override fun onDeleteClickLIstner(position: Int) {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.remove_prof_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
        val dialog = mBuilder.show()

        dialogView.dialog_remove.setOnClickListener{
            removeProfile(position)
            dialog.dismiss()
        }
        dialogView.dialog_cancel.setOnClickListener{
            dialog.dismiss()
        }

    }


    fun removeProfile(position: Int) {
        if(removeProfileFromServer(profiles.get(position).deviceId.toString())) {
            db.removeProfile( profiles.get(position).deviceId)
            profiles_recycler.adapter!!.notifyItemRemoved(position)
            profiles = db.listAllProfiles()
            profiles_recycler.adapter = UserProfileAdapter(profiles, this)
            profiles_recycler.layoutManager = LinearLayoutManager(this)
            Toast.makeText(this, "Profile was removed", Toast.LENGTH_LONG).show()

        } else {
            Toast.makeText(this, "Error ! The profile was not removed", Toast.LENGTH_LONG).show()

        }
    }

    fun removeProfileFromServer(deviceId: String): Boolean{
        var isDeleted = false
        val challenge = UUID.randomUUID().toString()
        val signature = BiometricAuthUtil.signChallenge(db.getPrivateKey(deviceId), challenge)

        val requestBody: RequestBody = FormBody.Builder()
            .addEncoded("deviceId", deviceId)
            .addEncoded("ACTION","DELETE")
            .addEncoded("signature", signature)
            .addEncoded("challenge", challenge)
            .build()


        val request: Request = Request.Builder()
            .url(BiometricAppConstants.DOMAIN_NAME + BiometricAppConstants.BIOMETRIC_ENDPOINT)
            .addHeader("ContentType", "application/x-www-form-urlencoded")
            .post(requestBody)
            .build()
        var client: OkHttpClient = DeviecRegistrationService().getUnsafeOkHttpClient()
        val countDownLatch = CountDownLatch(1)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                countDownLatch.countDown()
            }
            override fun onResponse(call: Call, response: Response) {
                if(response.code == 200) {
                    isDeleted = true
                }
                countDownLatch.countDown()
            }
        })
        countDownLatch.await()
        return isDeleted
    }

}
