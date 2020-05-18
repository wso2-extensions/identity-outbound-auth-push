package org.wso2.carbon.identity.mobile.wso2verify.Model

class AuthResponse {
    var status: String? = null
        get() = field
        set(value) {
            field = value
        }

    constructor(status: String?) {
        this.status = status
    }

    constructor()

}