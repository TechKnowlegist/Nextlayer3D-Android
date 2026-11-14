package com.nextlayer3d.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nextlayer3d.app.state.SessionViewModel
import com.nextlayer3d.app.ui.account.AuthMode
import com.nextlayer3d.app.ui.account.AuthScreen
import com.nextlayer3d.app.ui.theme.NextlayerLogo
import com.nextlayer3d.app.ui.theme.NlGreen
import com.nextlayer3d.app.ui.theme.NlPurple
import com.nextlayer3d.app.ui.theme.NlSky

/** What NextlayerApp shows in place of the whole tabbed app whenever
 * session.isSignedIn is false — the first thing a new install sees, and
 * what reappears after signing out. Starts on WelcomeScreen; either
 * button drops into AuthScreen pre-set to the matching mode, with a back
 * arrow to return here. */
@Composable
fun AuthGateScreen(session: SessionViewModel) {
    var authMode by remember { mutableStateOf<AuthMode?>(null) }
    val mode = authMode
    if (mode == null) {
        WelcomeScreen(
            onCreateAccount = { authMode = AuthMode.SIGN_UP },
            onSignIn = { authMode = AuthMode.SIGN_IN },
        )
    } else {
        AuthScreen(session = session, initialMode = mode, onBack = { authMode = null })
    }
}

@Composable
private fun WelcomeScreen(onCreateAccount: () -> Unit, onSignIn: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        NextlayerLogo(size = 72.dp)
        Spacer(Modifier.height(28.dp))
        Text(
            text = "Layer by layer.\nIdea to object.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                brush = Brush.linearGradient(listOf(NlGreen, NlSky, NlPurple)),
            ),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Design it, we print it in real filament, and you can track it the whole way — right from your phone.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onCreateAccount,
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Text("Create Account", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onSignIn,
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Text("Sign In", style = MaterialTheme.typography.titleMedium)
        }
    }
}
