package com.nextlayer3d.app.ui.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nextlayer3d.app.data.AddressService
import com.nextlayer3d.app.models.Address
import com.nextlayer3d.app.state.SessionViewModel
import kotlinx.coroutines.launch

@Composable
fun AccountScreen(session: SessionViewModel, onAddAddress: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Account") }) }) { padding ->
        Box(Modifier.padding(padding)) {
            if (session.isSignedIn) {
                SignedInAccount(session = session, onAddAddress = onAddAddress)
            } else {
                AuthScreen(session = session)
            }
        }
    }
}

@Composable
private fun SignedInAccount(session: SessionViewModel, onAddAddress: () -> Unit) {
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
