package org.wso2.carbon.identity.mobile.wso2verify.Model

class RegistrationRequestDTO {
    var id: String? = null
        get() = field
        set(value) {
            field = value
        }
    var name: String? = null
        get() = field
        set(value) {
            field = value
        }
    var model: String? = null
        get() = field
        set(value) {
            field = value
        }
    var pushId: String? = null
        get() = field
        set(value) {
            field = value
        }
    var publickey: String? = null
        get() = field
        set(value) {
            field = value
        }
    var signature: String? = null
        get() = field
        set(value) {
            field = value
        }

    constructor(
        id: String?,
        name: String?,
        model: String?,
        pushId: String?,
        publickey: String?,
        signature: String?
    ) {
        this.id = id
        this.name = name
        this.model = model
        this.pushId = pushId
        this.publickey = publickey
        this.signature = signature
    }

    constructor()


}