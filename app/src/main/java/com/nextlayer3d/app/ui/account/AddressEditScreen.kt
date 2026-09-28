package com.nextlayer3d.app.ui.account

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nextlayer3d.app.data.AddressService
import com.nextlayer3d.app.models.AddressInput
import kotlinx.coroutines.launch

@Composable
fun AddressEditScreen(onSaved: () -> Unit) {
    var label by remember { mutableStateOf("Home") }
    var fullName by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var zip by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("United States") }
    var isDefault by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val formIsValid = fullName.isNotBlank() && street.isNotBlank() && city.isNotBlank() && state.isNotBlank() && zip.isNotBlank()

    Scaffold(topBar = { TopAppBar(title = { Text("New Address") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(label, { label = it }, Modifier.fillMaxWidth(), label = { Text("Label (e.g. Home, Work)") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(fullName, { fullName = it }, Modifier.fillMaxWidth(), label = { Text("Full name") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(street, { street = it }, Modifier.fillMaxWidth(), label = { Text("Street") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(city, { city = it }, Modifier.fillMaxWidth(), label = { Text("City") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(state, { state = it }, Modifier.fillMaxWidth(), label = { Text("State") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(zip, { zip = it }, Modifier.fillMaxWidth(), label = { Text("ZIP") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(country, { country = it }, Modifier.fillMaxWidth(), label = { Text("Country") })
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
                Text("Default address")
            }

            errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        isSaving = true
                        errorMessage = null
                        try {
                            AddressService.create(AddressInput(label, fullName, street, city, state, zip, country, isDefault))
                            onSaved()
                        } catch (e: Exception) {
                            errorMessage = e.message
                        }
                        isSaving = false
                    }
                },
                enabled = formIsValid && !isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (isSaving) "Saving…" else "Save Address")
            }
        }
    }
}
