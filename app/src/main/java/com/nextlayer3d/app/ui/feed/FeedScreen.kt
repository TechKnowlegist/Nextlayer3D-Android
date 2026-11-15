package com.nextlayer3d.app.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nextlayer3d.app.ui.theme.GradientText

/** Honest placeholder, not a fake gallery — this is the community gallery
 * the website calls "Build Plate" (browse/share what people have
 * printed), renamed "Feed" for this app's tab bar. It hasn't been ported
 * yet, same as the 3D Editor, Minecraft voxelizer, LayerAI, AR viewer,
 * and Showroom (see README's "Not yet ported" list) — this tab exists
 * because the navigation asked for it, not because the feature is real
 * here yet. */
@Composable
fun FeedScreen() {
    Scaffold(topBar = { TopAppBar(title = { GradientText("Feed") }) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                Icons.Filled.DynamicFeed,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
            Text("Feed is coming", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(
                "The community gallery — browse and share what people have printed (\"Build Plate\" on the website) " +
                    "isn't built here yet. This app gets it in a later phase.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
