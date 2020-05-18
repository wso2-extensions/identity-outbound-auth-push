package org.wso2.carbon.identity.mobile.wso2verify

import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.wait
import org.wso2.carbon.identity.mobile.wso2verify.Model.BiometricAuthProfile
import org.wso2.carbon.identity.mobile.wso2verify.Model.DiscoveryDataDTO
import org.wso2.carbon.identity.mobile.wso2verify.Model.RegistrationRequestDTO
import java.io.IOException
import java.security.*
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import java.util.concurrent.CountDownLatch
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.collections.HashMap

class DeviecRegistrationService {
     fun getUnsafeOkHttpClient(): OkHttpClient {
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
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .build()
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
        var instanceId = FirebaseInstanceId.getInstance().token
        return RegistrationRequestDTO(id, model.toUpperCase(), model, instanceId, publicKey, signature)
    }
    private fun signChallenge(privateKey: String, challenge: UUID?): String{
        var keyspec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey))
        var pk: PrivateKey = KeyFactory.getInstance("DSA").generatePrivate(keyspec)

        var sign = Signature.getInstance("Sha256withDSA")
        sign.initSign(pk)
        sign.update(challenge.toString().toByteArray())

        return Base64.getEncoder().encodeToString(sign.sign())

    }

    fun sendRegistrationRequest(discoveryData: DiscoveryDataDTO, context: Context) {
        var client: OkHttpClient = getUnsafeOkHttpClient()
        var keyPair = getKeyPair()
        var registrationRequest = getRegistrationRequest(
            discoveryData.id, keyPair.getValue("public"),
            signChallenge(keyPair.getValue("private"), discoveryData.challenge)
        )
        var json = Gson().toJson(registrationRequest)
        var requestBody: RequestBody =
            json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request: Request = Request.Builder()
            .url(discoveryData.registrationUrl)
            .post(requestBody)
            .build()

        val countDownLatch = CountDownLatch(1)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val intent = Intent(context, RegistrationFailedActivity::class.java)
                context.startActivity(intent)
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
               if(response.code == 200) {
                   var profile: BiometricAuthProfile = BiometricAuthProfile(
                       discoveryData.id,
                       discoveryData.username,
                       discoveryData.tennantDomain,
                       discoveryData.userStoreDomain,
                       discoveryData.authenticationUrl,
                       keyPair.getValue("private")
                   )
//                   DatabaseHelper(context).addBiometricProfile(profile)
                   val intent = Intent(context, RegistrationSuccessActivity::class.java)
                   context.startActivity(intent)
                   countDownLatch.countDown()
               } else {
                   val intent = Intent(context, RegistrationFailedActivity::class.java)
                   context.startActivity(intent)
                   countDownLatch.countDown()
               }
            }
        })
        countDownLatch.countDown()

    }
}