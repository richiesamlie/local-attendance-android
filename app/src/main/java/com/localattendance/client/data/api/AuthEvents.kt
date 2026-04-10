package com.localattendance.client.data.api

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthEvent {
    data object SessionExpired : AuthEvent()
}

@Singleton
class AuthEvents @Inject constructor() {
    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    suspend fun emitSessionExpired() {
        _events.emit(AuthEvent.SessionExpired)
    }
}