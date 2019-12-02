package org.wso2.carbon.identity.mobile.wso2verify.util

interface RequestUrlBuilder {

    fun requestUrlBuilder(sessionDataKey: String?, challenge:String?): String
}
