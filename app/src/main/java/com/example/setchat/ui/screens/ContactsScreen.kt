package com.example.setchat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.setchat.data.local.Contact
import com.example.setchat.ui.components.AvatarCircle

@Composable
fun ContactsScreen(
    contacts: List<Contact>,
    onBack: () -> Unit,
    onAddContact: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F2F5)).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onBack) { Text("Retour") }
            TextButton(onClick = {
                name = ""
                role = ""
                status = ""
            }) { Text("Vider") }
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Statut visible") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onAddContact(name, role, status); name = ""; role = ""; status = "" },
            enabled = name.isNotBlank()
        ) { Text("Ajouter le contact") }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(contacts) { contact ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        AvatarCircle(
                            label = contact.avatarText.ifBlank { contact.name.take(1).uppercase() },
                            imageUri = if (contact.avatarType == "photo") contact.avatarUri else ""
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                            Text(contact.name, fontWeight = FontWeight.SemiBold)
                            Text(contact.role, color = Color(0xFF667781))
                            Text(contact.status, color = Color(0xFF667781))
                        }
                    }
                }
            }
        }
    }
}
