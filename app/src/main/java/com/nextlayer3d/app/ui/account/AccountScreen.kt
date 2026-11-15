package com.nextlayer3d.app.ui.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nextlayer3d.app.data.AddressService
import com.nextlayer3d.app.models.Address
import com.nextlayer3d.app.state.SessionViewModel
import com.nextlayer3d.app.ui.theme.GradientButton
import com.nextlayer3d.app.ui.theme.GradientText
import kotlinx.coroutines.launch

// NextlayerApp now lets someone reach the tabbed app as a guest
// ("Continue as Guest" on the welcome screen), so Hub can be opened
// without session.isSignedIn being true — in that case it shows a
// sign-in prompt instead of SignedInAccount, since orders/addresses need
// a real account. Labeled "Hub" in the tab bar — account info, order
// history, and (later) settings all live here instead of each getting
// their own tab.
@Composable
fun AccountScreen(
    session: SessionViewModel,
    onAddAddress: () -> Unit,
    onViewOrders: () -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
) {
    Scaffold(topBar = { TopAppBar(title = { GradientText("Hub") }) }) { padding ->
        Box(Modifier.padding(padding)) {
            if (session.isSignedIn) {
                SignedInAccount(session = session, onAddAddress = onAddAddress, onViewOrders = onViewOrders)
            } else {
                GuestAccount(onSignIn = onSignIn, onSignUp = onSignUp)
            }
        }
    }
}

@Composable
private fun GuestAccount(onSignIn: () -> Unit, onSignUp: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Filled.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))
        Text("You're browsing as a guest", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            "Sign in to see your orders, saved addresses, and account details.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        GradientButton(onClick = onSignUp, modifier = Modifier.fillMaxWidth()) {
            Text("Create Account", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onSignIn, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Sign In", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun SignedInAccount(session: SessionViewModel, onAddAddress: () -> Unit, onViewOrders: () -> Unit) {
    var addresses by remember { mutableStateOf<List<Address>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun load() {
        scope.launch {
            isLoading = true
            runCatching { AddressService.list() }
                .onSuccess { addresses = it; errorMessage = null }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { load() }

    LazyColumn(Modifier.padding(16.dp)) {
        item {
            Text(session.email ?: "Signed in", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { session.signOut() }) { Text("Sign Out") }
            Spacer(Modifier.height(20.dp))
            Card(onClick = onViewOrders, modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text("My Orders", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Saved Addresses", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
        }

        if (isLoading) {
            item { CircularProgressIndicator() }
        } else if (addresses.isEmpty()) {
            item { Text("No saved addresses yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(addresses, key = { it.id }) { address ->
                Column(Modifier.padding(vertical = 8.dp)) {
                    Text(address.label ?: address.fullName ?: "Address", style = MaterialTheme.typography.bodyLarge)
                    Text(address.displayLine, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        errorMessage?.let { message ->
            item { Text(message, color = MaterialTheme.colorScheme.error) }
        }

        item {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onAddAddress) { Text("Add Address") }
        }
    }
}
