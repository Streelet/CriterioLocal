package com.example.criteriolocal.core.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager {

    private val _userId = MutableStateFlow<Long?>(null)
    val userId: StateFlow<Long?> = _userId.asStateFlow()

    val currentUserId: Long?
        get() = _userId.value

    fun setUserId(id: Long) {
        _userId.value = id
    }

    fun clear() {
        _userId.value = null
    }
}
