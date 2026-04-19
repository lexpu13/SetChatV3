package com.example.setchat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.setchat.data.local.Message
import com.example.setchat.ui.components.AvatarCircle
import com.example.setchat.ui.components.ChatBubble

@Composable
fun ChatScreen(
    conversationTitle: String,
    conversationSubtitle: String,
    isGroup: Boolean,
    messages: List<Message>,
    onBack: () -> Unit,
    onSendMine: (String) -> Unit,
    onSendOther: (String, String) -> Unit,
    onOpenAudioCall: () -> Unit = {},
    onOpenVideoCall: () -> Unit = {},
    isDarkTheme: Boolean = true
) {
    var text by remember { mutableStateOf("") }
    var incomingSender by remember { mutableStateOf(if (isGroup) "" else conversationTitle) }
    val pageBg = if (isDarkTheme) Color(0xFF0B141A) else Color(0xFFEAE6DF)
    val topBarBg = if (isDarkTheme) Color(0xFF1B4F8A) else Color(0xFF0B8F68)
    val topSubtitle = if (isDarkTheme) Color(0xFFBFD8F8) else Color(0xFFD7F5EC)
    val inputBg = if (isDarkTheme) Color(0xFF111B21) else Color(0xFFF0F2F5)
    val inputIcon = if (isDarkTheme) Color(0xFF8EA5BC) else Color(0xFF667781)
    val micTint = if (isDarkTheme) Color(0xFF62B0FF) else Color(0xFF0B8F68)
    val hintText = if (isDarkTheme) Color(0xFF8EA5BC) else Color(0xFF667781)

    Column(modifier = Modifier.fillMaxSize().background(pageBg)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(topBarBg)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            }
            AvatarCircle(if (isGroup) "👥" else conversationTitle.take(1).uppercase())
            Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                Text(conversationTitle, color = Color.White, fontWeight = FontWeight.Bold)
                Text(conversationSubtitle, color = topSubtitle)
            }
            IconButton(onClick = onOpenAudioCall) { Icon(Icons.Default.Call, contentDescription = null, tint = Color.White) }
            IconButton(onClick = onOpenVideoCall) { Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White) }
            IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White) }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 10.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(messages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isMine) Arrangement.End else Arrangement.Start
                ) {
                    Box(modifier = Modifier.fillMaxWidth(0.82f)) {
                        ChatBubble(
                            message = msg,
                            isDarkTheme = isDarkTheme,
                            showTimestamp = true
                        )
                    }
                }
            }
        }

        Surface(color = inputBg, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Ecrire un message") },
                        shape = RoundedCornerShape(28.dp)
                    )
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = inputIcon)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (isGroup) {
                    OutlinedTextField(
                        value = incomingSender,
                        onValueChange = { incomingSender = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Personnage qui parle") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            onSendMine(text)
                            text = ""
                        },
                        enabled = text.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Text(" Envoyer")
                    }
                    Button(
                        onClick = {
                            onSendOther(text, if (isGroup) incomingSender else conversationTitle)
                            text = ""
                        },
                        enabled = text.isNotBlank() && (!isGroup || incomingSender.isNotBlank()),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text("Personnage")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = micTint)
                    }
                }
                Text(
                    text = if (isGroup) {
                        "Utilise le champ du dessus pour choisir quel personnage envoie le message entrant."
                    } else {
                        "Le bouton Personnage ajoute un message recu au nom du contact."
                    },
                    color = hintText,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}
