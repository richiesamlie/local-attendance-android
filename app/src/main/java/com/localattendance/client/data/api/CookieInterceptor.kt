package com.localattendance.client.data.api

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CookieInterceptor @Inject constructor(
    context: Context
) : Interceptor {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "cookie_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        val savedCookies = prefs.getString("auth_token_cookie", null)
        if (savedCookies != null) {
            requestBuilder.addHeader("Cookie", savedCookies)
        }

        val response = chain.proceed(requestBuilder.build())

        val setCookieHeader = response.header("Set-Cookie")
        if (setCookieHeader != null) {
            val cookieValue = setCookieHeader.split(";")[0]
            prefs.edit().putString("auth_token_cookie", cookieValue).apply()
        }

        return response
    }
}
