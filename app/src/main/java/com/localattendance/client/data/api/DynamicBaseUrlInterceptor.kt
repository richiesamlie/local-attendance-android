package com.localattendance.client.data.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import com.localattendance.client.data.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.runBlocking

class DynamicBaseUrlInterceptor @Inject constructor(
    private val settingsRepository: SettingsRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // Get current server URL from DataStore (synchronously for the interceptor)
        val baseUrl = runBlocking {
            // We need to get the latest value from the flow
            // This is a bit hacky but necessary for OkHttp interceptors
            var result: String? = null
            settingsRepository.serverUrl.collect { result = it }
            // Note: The above is a blocking call, but since it's local DataStore, it's fast.
            // Better way: Use a singleton state holder.
            result
        } ?: "http://placeholder.com/"

        val url = request.url
        val newUrl = url.newBuilder()
            .host(baseUrl.removePrefix("http://").removePrefix("https://").split("/")[0])
            .build()

        // If the request was sent to placeholder, rewrite it to the actual server
        if (url.host == "placeholder.com") {
            request = request.newBuilder()
                .url(newUrl)
                .build()
        }

        return chain.proceed(request)
    }
}
