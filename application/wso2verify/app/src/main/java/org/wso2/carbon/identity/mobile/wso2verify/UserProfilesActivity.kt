package org.wso2.carbon.identity.mobile.wso2verify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_user_profiles.*
import org.wso2.carbon.identity.mobile.wso2verify.Adapters.UserProfileAdapter
import org.wso2.carbon.identity.mobile.wso2verify.Model.BiometricAuthProfile

class UserProfiles : AppCompatActivity() {
    internal lateinit var db: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        db = DatabaseHelper(this)
        val profiles: ArrayList<BiometricAuthProfile> = db.listAllProfiles()
        profiles_recycler.adapter = UserProfileAdapter(profiles)
        profiles_recycler.layoutManager = LinearLayoutManager(this)
        profiles_recycler.setHasFixedSize(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profiles)
    }
}
