package com.nextlayer3d.app.ui.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.nextlayer3d.app.data.AuthService
import com.nextlayer3d.app.state.SessionViewModel
import kotlinx.coroutines.launch

private enum class AuthMode { SIGN_IN, SIGN_UP, CONFIRM }

/** Combined sign-in / sign-up / confirm-code flow — mirrors the web app's
 * single AuthModal and the iOS app's AuthView. */
@Composable
fun AuthScreen(session: SessionViewModel) {
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submit() {
        scope.launch {
            errorMessage = null
            isLoading = true
            try {
                when (mode) {
                    AuthMode.SIGN_IN -> {
                        if (AuthService.signIn(email, password)) session.refresh()
                    }
                    AuthMode.SIGN_UP -> {
                        mode = if (AuthService.signUp(email, password)) AuthMode.SIGN_IN else AuthMode.CONFIRM
                    }
                    AuthMode.CONFIRM -> {
                        if (AuthService.confirmSignUp(email, code)) {
                            mode = AuthMode.SIGN_IN
                            errorMessage = "Account confirmed — sign in below."
                        }
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.message
            }
            isLoading = false
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text(if (mode == AuthMode.SIGN_UP) "Create Account" else "Sign In", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        if (mode != AuthMode.CONFIRM) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text("Check your email for a confirmation code")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Confirmation code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = ::submit, enabled = !isLoading, modifier = Modifier.fillMaxWidth()) {
            Text(
                when (mode) {
                    AuthMode.SIGN_IN -> "Sign In"
                    AuthMode.SIGN_UP -> "Create Account"
                    AuthMode.CONFIRM -> "Confirm"
                }
            )
        }

        if (mode != AuthMode.CONFIRM) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = {
                mode = if (mode == AuthMode.SIGN_IN) AuthMode.SIGN_UP else AuthMode.SIGN_IN
                errorMessage = null
            }) {
                Text(if (mode == AuthMode.SIGN_IN) "Need an account? Sign up" else "Already have an account? Sign in")
            }
        }
    }
}
