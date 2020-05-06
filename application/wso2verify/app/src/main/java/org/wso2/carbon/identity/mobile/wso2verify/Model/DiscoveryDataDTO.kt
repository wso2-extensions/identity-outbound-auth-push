package org.wso2.carbon.identity.mobile.wso2verify.Model

import java.util.*

class DiscoveryDataDTO{
     var id: String? = null

     var username: String? = null
         get() = field
         set(value) {
             field = value
         }
    var tennantDomain: String? = null
        get() = field
        set(value) {
            field = value
        }
    var userStoreDomain: String? = null
        get() = field
        set(value) {
            field = value
        }
    var registrationUrl: String? = null
        get() = field
        set(value) {
            field = value
        }
     var authenticationUrl: String? = null
        get() = field
        set(value) {
            field = value
        }
     var challenge: UUID? = null
        get() = field
        set(value) {
            field = value
        }


    constructor(
        id: String,
        username: String,
        tennantDomain: String,
        userStoreDomain: String,
        registrationUrl: String,
        authenticationUrl: String,
        challenge: UUID
    ) {
        this.id = id
        this.username = username
        this.tennantDomain = tennantDomain
        this.userStoreDomain = userStoreDomain
        this.registrationUrl = registrationUrl
        this.authenticationUrl = authenticationUrl
        this.challenge = challenge
    }

    constructor()
}