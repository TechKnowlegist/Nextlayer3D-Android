package com.nextlayer3d.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.nextlayer3d.app.ui.theme.GradientButton
import com.nextlayer3d.app.ui.theme.GradientText
import com.nextlayer3d.app.ui.theme.NlGreen
import com.nextlayer3d.app.ui.theme.NlPurple
import com.nextlayer3d.app.ui.theme.NlSky

/** What NextlayerApp shows in place of the whole tabbed app whenever
 * session.isSignedIn is false and the person hasn't chosen to browse as a
 * guest — the first thing a new install sees, and what reappears after
 * signing out. Starts on WelcomeScreen; either button drops into
 * AuthScreen pre-set to the matching mode, with a back arrow to return
 * here; "Continue as Guest" skips the login flow entirely. */
@Composable
fun AuthGateScreen(session: SessionViewModel, onContinueAsGuest: () -> Unit) {
    var authMode by remember { mutableStateOf<AuthMode?>(null) }
    val mode = authMode
    if (mode == null) {
        WelcomeScreen(
            onCreateAccount = { authMode = AuthMode.SIGN_UP },
            onSignIn = { authMode = AuthMode.SIGN_IN },
            onContinueAsGuest = onContinueAsGuest,
        )
    } else {
        AuthScreen(session = session, initialMode = mode, onBack = { authMode = null })
    }
}

@Composable
private fun WelcomeScreen(onCreateAccount: () -> Unit, onSignIn: () -> Unit, onContinueAsGuest: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Ambient green/sky/purple glow behind the content — the same
        // colorful-glow-behind-the-headline treatment the website's hero
        // sections use, instead of a flat background.
        Box(
            Modifier
                .align(Alignment.TopStart)
                .offset(x = (-90).dp, y = (-60).dp)
                .size(260.dp)
                .background(Brush.radialGradient(listOf(NlGreen.copy(alpha = 0.22f), androidx.compose.ui.graphics.Color.Transparent)), CircleShape),
        )
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = 20.dp)
                .size(220.dp)
                .background(Brush.radialGradient(listOf(NlSky.copy(alpha = 0.18f), androidx.compose.ui.graphics.Color.Transparent)), CircleShape),
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .size(300.dp)
                .background(Brush.radialGradient(listOf(NlPurple.copy(alpha = 0.20f), androidx.compose.ui.graphics.Color.Transparent)), CircleShape),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            com.nextlayer3d.app.ui.theme.NextlayerLogo(size = 72.dp)
            Spacer(Modifier.height(28.dp))
            GradientText(
                text = "Layer by layer.\nIdea to object.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Design it, we print it in real filament, and you can track it the whole way — right from your phone.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(40.dp))
            GradientButton(
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
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onContinueAsGuest) {
                Text("Continue as Guest")
            }
        }
    }
}
