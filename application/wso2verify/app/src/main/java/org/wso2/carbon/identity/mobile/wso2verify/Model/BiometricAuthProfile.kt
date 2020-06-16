package org.wso2.carbon.identity.mobile.wso2verify.Model


class BiometricAuthProfile {
    var deviceId: String? = null
        get() = field
        set(value) {
            field = value
        }
    var username: String? = null
        get() = field
        set(value) {
            field = value
        }
    var tenantDomain: String? = null
        get() = field
        set(value) {
            field = value
        }
    var userStore: String? = null
        get() = field
        set(value) {
            field = value
        }
    var authUrl: String = ""
        get() = field
        set(value) {
            field = value
        }
    var privateKey: String = ""
        get() = field
        set(value) {
            field = value
        }

    constructor(
        deviceId: String?,
        username: String?,
        tenantDomain: String?,
        userStore: String?,
        authUrl: String,
        privateKey: String
    ) {
        this.deviceId = deviceId
        this.username = username
        this.tenantDomain = tenantDomain
        this.userStore = userStore
        this.authUrl = authUrl
        this.privateKey = privateKey
    }

    constructor()

}