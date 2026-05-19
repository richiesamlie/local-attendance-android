package com.localattendance.client.data.api

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.HttpUrl
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("DEPRECATION")
@Singleton
class SessionCookieStore @Inject constructor(
    @ApplicationContext context: Context
) {
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

    fun getCookie(url: HttpUrl): String? {
        return prefs.getString(keyFor(url), null)
    }

    fun saveCookie(url: HttpUrl, setCookieHeader: String) {
        val cookieValue = setCookieHeader.substringBefore(";").trim()
        if (cookieValue.isBlank()) {
            return
        }
        prefs.edit()
            .remove(LEGACY_COOKIE_KEY)
            .putString(keyFor(url), cookieValue)
            .apply()
    }

    fun clearCookie(url: HttpUrl) {
        prefs.edit()
            .remove(LEGACY_COOKIE_KEY)
            .remove(keyFor(url))
            .apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    private fun keyFor(url: HttpUrl): String {
        return "auth_token_cookie:${url.scheme}://${url.host}:${url.port}"
    }

    private companion object {
        const val LEGACY_COOKIE_KEY = "auth_token_cookie"
    }
}
