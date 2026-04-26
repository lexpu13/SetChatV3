package com.example.setchat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val pageBg = if (isDarkTheme) Color(0xFF0A0F14) else Color(0xFFEAE6DF)
    val topBarBg = if (isDarkTheme) Color(0xFF0E1217) else Color(0xFF0B8F68)
    val topSubtitle = if (isDarkTheme) Color(0xFFAAB8C6) else Color(0xFFD7F5EC)
    val inputBg = if (isDarkTheme) Color(0xFF11171D) else Color(0xFFF0F2F5)
    val composerBg = if (isDarkTheme) Color(0xFF1D232A) else Color(0xFFFFFFFF)
    val inputText = if (isDarkTheme) Color(0xFFE9F1FA) else Color(0xFF111B21)
    val inputHint = if (isDarkTheme) Color(0xFF8FA2B6) else Color(0xFF667781)
    val iconColor = if (isDarkTheme) Color(0xFFCFDBE8) else Color(0xFF4D6073)
    val accent = if (isDarkTheme) Color(0xFF2D8CFF) else Color(0xFF0B8F68)
    val dayBg = if (isDarkTheme) Color(0xFF1B2229) else Color(0xFFE7EEF5)
    val dayText = if (isDarkTheme) Color(0xFF9FB1C2) else Color(0xFF5A7288)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(topBarBg)
                .padding(start = 2.dp, end = 6.dp, top = 6.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color.White
                )
            }
            AvatarCircle(
                label = if (isGroup) "👥" else conversationTitle.take(1).uppercase(),
                size = 34.dp
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = conversationTitle,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = conversationSubtitle,
                    color = topSubtitle,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
            IconButton(onClick = onOpenAudioCall) {
                Icon(Icons.Default.Call, contentDescription = null, tint = Color.White)
            }
            IconButton(onClick = onOpenVideoCall) {
                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(messages) { msg ->
                if (msg.kind == "day") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            color = dayBg,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = msg.text.ifBlank { "Aujourd'hui" },
                                color = dayText,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                } else {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val bubbleMaxWidth = maxWidth * 0.84f
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.isMine) Arrangement.End else Arrangement.Start,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            ChatBubble(
                                modifier = Modifier.widthIn(max = bubbleMaxWidth),
                                message = msg,
                                isDarkTheme = isDarkTheme,
                                showTimestamp = true,
                                showSenderName = isGroup && !msg.isMine,
                                showDeliveryIcon = false
                            )
                        }
                    }
                }
            }
        }

        Surface(
            color = inputBg,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = CircleShape, color = Color.Transparent) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = iconColor)
                    }
                }
                Surface(
                    color = composerBg,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (text.isBlank()) {
                                Text(
                                    text = "Message",
                                    color = inputHint
                                )
                            }
                            BasicTextField(
                                value = text,
                                onValueChange = { text = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = inputText,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.AttachFile, contentDescription = null, tint = iconColor)
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = iconColor)
                        }
                    }
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = iconColor)
                }
                IconButton(
                    onClick = {
                        val payload = text.trim()
                        if (payload.isBlank()) return@IconButton
                        if (isGroup && payload.startsWith("~") && payload.contains(":")) {
                            val sender = payload.substringAfter("~").substringBefore(":").trim()
                            val body = payload.substringAfter(":").trim()
                            if (sender.isNotBlank() && body.isNotBlank()) {
                                onSendOther(body, sender)
                            } else {
                                onSendMine(payload)
                            }
                        } else {
                            onSendMine(payload)
                        }
                        text = ""
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = accent)
                }
            }
        }
    }
}
