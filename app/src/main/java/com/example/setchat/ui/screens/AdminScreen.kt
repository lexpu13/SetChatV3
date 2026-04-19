package com.example.setchat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AdminScreen(
    onBack: () -> Unit,
    onCreateConversation: (String, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var isGroup by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF0F2F5)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = onBack) { Text("Retour") }
        Text("Creer une discussion")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !isGroup, onClick = { isGroup = false }, label = { Text("Privee") })
            FilterChip(selected = isGroup, onClick = { isGroup = true }, label = { Text("Groupe") })
        }
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(if (isGroup) "Nom du groupe" else "Nom du contact / chat") }
        )
        Button(onClick = { onCreateConversation(title, isGroup); title = "" }, enabled = title.isNotBlank()) {
            Text("Creer")
        }
        Text(
            text = if (isGroup) {
                "Le groupe apparaitra dans la liste avec des messages factices."
            } else {
                "Le nouveau chat prive sera pret a recevoir des messages."
            },
            color = Color(0xFF667781)
        )
    }
}
