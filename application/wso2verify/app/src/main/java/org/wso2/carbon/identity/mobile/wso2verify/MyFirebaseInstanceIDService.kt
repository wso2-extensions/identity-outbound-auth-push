package org.wso2.carbon.identity.mobile.wso2verify

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Class which is called when firebase instance device IDs are refreshed.
 */
class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {
    val TAG = "Firebase"
    override fun onTokenRefresh() {

        Log.d(TAG, "Refreshed token:  " + FirebaseInstanceId.getInstance().getToken())
    }
    //todo register the available device IDs once the registration phase is done.-DOCS

}
