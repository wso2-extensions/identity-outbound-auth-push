package org.wso2.carbon.identity.mobile.wso2verify

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.Intent

/**
 * Class which handles the Firebase messaging service.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = "FirebaseMessagingService"

    override fun onMessageReceived(remoteMessage: RemoteMessage){

        Log.d(TAG, "remoteMessage: ${remoteMessage.from}")
        Log.e(TAG, "From: " + remoteMessage.data.toString())

        if (remoteMessage.notification != null) {
            remoteMessage.data.isNotEmpty().let {
                remoteMessage.notification.clickAction

                val sessionDataKey= remoteMessage.data[BiometricAppConstants.CONTEXT_KEY]
                val challenge= remoteMessage.data[BiometricAppConstants.CHALLENGE]
                val clickAction= remoteMessage.data[BiometricAppConstants.CLICK_ACTION]

                Log.d("TAG","session data key at firebase class : $sessionDataKey")
                Log.d("TAG","challenge at firebase class: $challenge")
                showNotification(
                    remoteMessage.notification?.body, sessionDataKey,challenge, clickAction)
            }
        }
    }
    private fun showNotification( body: String?,sessionDataKey:
    String?, challenge:String?, click_action:String?) {

        val intent =Intent(click_action)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(BiometricAppConstants.CONTEXT_KEY,sessionDataKey)
        intent.putExtra(BiometricAppConstants.CHALLENGE,challenge)
        intent.putExtra(BiometricAppConstants.BODY,body)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
