package com.nextlayer3d.app.ui.layerai

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Honest placeholder — LayerAI (describe something in words, get back a
 * printable 3D file) isn't connected to a real generation backend even on
 * the website yet (it needs a Meshy-or-similar API key wired into an
 * Amplify Function first), so there's nothing real to port here either.
 * Same "say so plainly" posture as Feed's placeholder. */
@Composable
fun LayerAiScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("LayerAI") }) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                Icons.Filled.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(Modifier.height(16.dp))
            Text("LayerAI is almost ready", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(
                "Describe something in words and get back a printable 3D file. This isn't connected to a real " +
                    "generator yet — not here, not on the website either — so there's nothing to try yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
