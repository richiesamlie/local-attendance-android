package com.localattendance.client.data.api

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CookieInterceptor @Inject constructor(
    private val cookieStore: SessionCookieStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        val savedCookies = cookieStore.getCookie(originalRequest.url)
        if (savedCookies != null) {
            requestBuilder.addHeader("Cookie", savedCookies)
        }

        val response = chain.proceed(requestBuilder.build())

        val setCookieHeader = response.header("Set-Cookie")
        if (setCookieHeader != null) {
            cookieStore.saveCookie(response.request.url, setCookieHeader)
        }

        return response
    }
}
