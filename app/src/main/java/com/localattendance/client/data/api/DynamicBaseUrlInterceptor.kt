package com.localattendance.client.data.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import com.localattendance.client.data.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

class DynamicBaseUrlInterceptor @Inject constructor(
    private val settingsRepository: SettingsRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val baseUrl = runBlocking {
            settingsRepository.serverUrl.first() ?: "http://placeholder.com/"
        }

        val url = request.url
        val newUrl = url.newBuilder()
            .host(baseUrl.removePrefix("http://").removePrefix("https://").split("/")[0])
            .build()

        if (url.host == "placeholder.com") {
            request = request.newBuilder()
                .url(newUrl)
                .build()
        }

        return chain.proceed(request)
    }
}
