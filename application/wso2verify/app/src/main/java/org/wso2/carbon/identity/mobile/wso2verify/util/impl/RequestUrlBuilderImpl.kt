package org.wso2.carbon.identity.mobile.wso2verify.util.impl

import android.util.Log

import org.wso2.carbon.identity.mobile.wso2verify.BiometricAppConstants
import org.wso2.carbon.identity.mobile.wso2verify.NetworkTask
import org.wso2.carbon.identity.mobile.wso2verify.util.RequestUrlBuilder

class RequestUrlBuilderImpl : RequestUrlBuilder {
    override fun requestUrlBuilder(sessionDataKey: String?, challenge: String?): String {

        val consent = BiometricAppConstants.SUCCESSFUL
        val requestURL= BiometricAppConstants.DOMAIN_NAME +
                BiometricAppConstants.BIOMETRIC_ENDPOINT + "?initiator=mobile&sessionDataKey=" +
                sessionDataKey + "&challenge=" + challenge
        Log.d("Request Url of the polling endpoint: " ,requestURL)
        NetworkTask().execute(requestURL, consent)
        return requestURL
    }
}
