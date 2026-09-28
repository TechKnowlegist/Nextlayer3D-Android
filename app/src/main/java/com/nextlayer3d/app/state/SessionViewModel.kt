package com.nextlayer3d.app.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextlayer3d.app.data.AuthService
import kotlinx.coroutines.launch

class SessionViewModel : ViewModel() {
    var isSignedIn by mutableStateOf(false)
        private set
    var email by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(true)
        private set

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            isSignedIn = AuthService.isSignedIn()
            email = if (isSignedIn) AuthService.currentUserEmail() else null
            isLoading = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            AuthService.signOut()
            isSignedIn = false
            email = null
        }
    }
}
