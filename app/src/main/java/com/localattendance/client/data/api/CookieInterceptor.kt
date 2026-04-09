package com.localattendance.client.data.api

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CookieInterceptor @Inject constructor(
    context: Context
) : Interceptor {
    private val prefs: SharedPreferences = context.getSharedPreferences("cookie_prefs", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // 1. Load persisted cookies from SharedPreferences
        val savedCookies = prefs.getString("auth_token_cookie", null)
        if (savedCookies != null) {
            requestBuilder.addHeader("Cookie", savedCookies)
        }

        val response = chain.proceed(requestBuilder.build())

        // 2. Save new cookies from response (Set-Cookie header)
        val setCookieHeader = response.header("Set-Cookie")
        if (setCookieHeader != null) {
            // The server sends a cookie like "auth_token=xyz; Path=/; HttpOnly"
            // We only need the key=value part
            val cookieValue = setCookieHeader.split(";")[0]
            prefs.edit().putString("auth_token_cookie", cookieValue).apply()
        }

        return response
    }
}
