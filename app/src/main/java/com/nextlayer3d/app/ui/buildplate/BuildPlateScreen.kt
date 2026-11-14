package com.nextlayer3d.app.ui.buildplate

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Honest placeholder, not a fake gallery — Build Plate (the community's
 * photo/model gallery on the website) hasn't been ported yet, same as
 * the 3D Editor, Minecraft voxelizer, AI, AR viewer, and Showroom (see
 * README's "Not yet ported" list). This tab exists now because the
 * navigation asked for it; the real feature is a later phase and needs
 * its own GraphQL queries this app doesn't have yet. */
@Composable
fun BuildPlateScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("Build Plate") }) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                Icons.Filled.PhotoCamera,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
            Text("Build Plate is coming", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(
                "The community gallery — browse and share what people have printed — isn't built here yet. " +
                    "It's on nextlayer3d.app today; this app gets it in a later phase.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
