package com.localattendance.client.data.api

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val authEvents: AuthEvents,
    private val cookieStore: SessionCookieStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == 401) {
            cookieStore.clearCookie(response.request.url)
            runBlocking {
                authEvents.emitSessionExpired()
            }
        }

        return response
    }
}
