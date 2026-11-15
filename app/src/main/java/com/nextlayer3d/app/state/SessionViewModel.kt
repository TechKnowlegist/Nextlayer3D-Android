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

    // Set by "Continue as Guest" on the welcome screen — lets someone
    // browse the tabbed app (Shop/Feed/Cart/LayerAI) without an account.
    // Hub falls back to a sign-in prompt instead of real account data
    // while this is true and isSignedIn is false.
    var isGuest by mutableStateOf(false)
        private set

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            isSignedIn = AuthService.isSignedIn()
            email = if (isSignedIn) AuthService.currentUserEmail() else null
            if (isSignedIn) isGuest = false
            isLoading = false
        }
    }

    fun continueAsGuest() {
        isGuest = true
    }

    fun signOut() {
        viewModelScope.launch {
            AuthService.signOut()
            isSignedIn = false
            email = null
            isGuest = false
        }
    }
}
