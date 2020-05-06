package org.wso2.carbon.identity.mobile.wso2verify

import android.os.Build
import com.google.firebase.iid.FirebaseInstanceId
import okhttp3.OkHttpClient
import org.wso2.carbon.identity.mobile.wso2verify.Model.DiscoveryDataDTO
import org.wso2.carbon.identity.mobile.wso2verify.Model.RegistrationRequestDTO
import java.security.*
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.collections.HashMap

class DeviecRegistrationService {
    private fun getUnsafeOkHttpClient(): OkHttpClient {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
        })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(HostnameVerifier { _, _ -> true }).build()
    }

    private fun getKeyPair(): HashMap<String, String>{
        var keyMap = HashMap<String, String>()
        var keyPairGenerator = KeyPairGenerator.getInstance("DSA")
        keyPairGenerator.initialize(2048)
        var pair: KeyPair = keyPairGenerator.genKeyPair()
        keyMap.put("public", Base64.getEncoder().encodeToString(pair.public.encoded))
        keyMap.put("private", Base64.getEncoder().encodeToString(pair.private.encoded))
        return keyMap
    }

    private fun getRegistrationRequest(id: String?, publicKey: String, signature: String): RegistrationRequestDTO{
        var model = Build.BRAND + " " + Build.MODEL
        var x = FirebaseInstanceId.getInstance().token
        var z = FirebaseInstanceId.getInstance().id
        return RegistrationRequestDTO(id, model.toUpperCase(), model, )
    }
    private fun signChallenge(privateKey: String, challenge: UUID?): String{
        var keyspec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey))
        var pk: PrivateKey = KeyFactory.getInstance("DSA").generatePrivate(keyspec)

        var sign = Signature.getInstance("Sha256withDSA")
        sign.initSign(pk)
        sign.update(challenge.toString().toByte())

        return String(Base64.getEncoder().encode(sign.sign()))

    }

    fun sendRegistrationRequest(discoveryData: DiscoveryDataDTO){
        var client = getUnsafeOkHttpClient()
        var keyPair = getKeyPair()
        var registrationRequest = getRegistrationRequest(discoveryData.id, keyPair.getValue("public"),
            signChallenge(keyPair.getValue("private"), discoveryData.challenge))


    }
}