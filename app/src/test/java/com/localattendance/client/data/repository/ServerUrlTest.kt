package com.localattendance.client.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ServerUrlTest {
    @Test
    fun normalizeServerUrl_acceptsHttpHostAndPort() {
        assertEquals("http://192.168.1.5:3000", normalizeServerUrl(" http://192.168.1.5:3000/ "))
    }

    @Test
    fun normalizeServerUrl_rejectsUnsupportedSchemes() {
        assertNull(normalizeServerUrl("ftp://192.168.1.5:3000"))
    }

    @Test
    fun normalizeServerUrl_rejectsMisleadingPaths() {
        assertNull(normalizeServerUrl("http://192.168.1.5:3000/api"))
    }
}
