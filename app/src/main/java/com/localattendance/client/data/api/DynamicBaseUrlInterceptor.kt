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
            settingsRepository.serverUrl.first() ?: return@runBlocking null
        }

        if (baseUrl == null || baseUrl.isBlank()) {
            return chain.proceed(request)
        }

        val validUrl = try {
            val parsed = java.net.URI(baseUrl)
            require(!parsed.host.isNullOrBlank()) { "Invalid host" }
            if (parsed.scheme != "http" && parsed.scheme != "https") {
                throw IllegalArgumentException("Invalid scheme")
            }
            parsed
        } catch (e: Exception) {
            return chain.proceed(request)
        }

        val url = request.url
        val newUrl = url.newBuilder()
            .host(validUrl.host)
            .port(validUrl.port.takeIf { it > 0 } ?: if (validUrl.scheme == "https") 443 else 80)
            .scheme(validUrl.scheme)
            .build()

        request = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(request)
    }
}
