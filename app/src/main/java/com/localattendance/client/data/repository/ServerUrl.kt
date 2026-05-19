package com.localattendance.client.data.repository

import java.net.URI

fun normalizeServerUrl(rawUrl: String): String? {
    val candidate = rawUrl.trim().trimEnd('/')
    if (candidate.isBlank()) {
        return null
    }

    val uri = try {
        URI(candidate)
    } catch (e: IllegalArgumentException) {
        return null
    }

    val scheme = uri.scheme?.lowercase()
    val host = uri.host?.lowercase()
    if (scheme != "http" && scheme != "https") {
        return null
    }
    if (host.isNullOrBlank()) {
        return null
    }
    if (uri.userInfo != null || uri.query != null || uri.fragment != null) {
        return null
    }
    if (!uri.path.isNullOrBlank() && uri.path != "/") {
        return null
    }

    val formattedHost = if (":" in host && !host.startsWith("[")) "[$host]" else host
    val port = uri.port.takeIf { it >= 0 }?.let { ":$it" } ?: ""
    return "$scheme://$formattedHost$port"
}
