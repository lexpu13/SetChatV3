package com.example.setchat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.setchat.data.local.Conversation
import com.example.setchat.ui.components.ConversationRow
import com.example.setchat.ui.components.MainTopBar

@Composable
fun HomeScreen(
    conversations: List<Conversation>,
    onOpenChat: (Long) -> Unit,
    onOpenContacts: () -> Unit,
    onOpenAdmin: () -> Unit,
    isDarkTheme: Boolean = true
) {
    val pageBg = if (isDarkTheme) Color(0xFF0B141A) else Color(0xFFF0F2F5)
    val fabColor = if (isDarkTheme) Color(0xFF1F7AE0) else Color(0xFF00A884)

    Column(modifier = Modifier.fillMaxSize().background(pageBg)) {
        MainTopBar(title = "BlinkChat", isDarkTheme = isDarkTheme)

        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(selected = false, onClick = onOpenContacts, label = { Text("Contacts") })
            FilterChip(selected = false, onClick = onOpenAdmin, label = { Text("Nouveau chat") })
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp)
        ) {
            items(conversations, key = { it.id }) { item ->
                ConversationRow(
                    item = item,
                    onClick = { onOpenChat(item.id) },
                    isDarkTheme = isDarkTheme
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End) {
            FloatingActionButton(onClick = onOpenAdmin, containerColor = fabColor) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
            }
        }
    }
}
