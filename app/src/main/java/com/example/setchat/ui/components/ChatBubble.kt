package com.example.setchat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.setchat.data.local.Message

@Composable
fun ChatBubble(
    message: Message,
    isDarkTheme: Boolean = true,
    showTimestamp: Boolean = true
) {
    val mineBubble = if (isDarkTheme) Color(0xFF1F7AE0) else Color(0xFFD9FDD3)
    val otherBubble = if (isDarkTheme) Color(0xFF202C33) else Color.White
    val mainText = if (isDarkTheme) Color(0xFFEAF2FF) else Color(0xFF111B21)
    val secondaryText = if (isDarkTheme) Color(0xFF9BB2C8) else Color(0xFF667781)
    val senderText = if (isDarkTheme) Color(0xFF7DD3FC) else Color(0xFF0B8F68)

    Column(
        modifier = Modifier
            .background(
                color = if (message.isMine) mineBubble else otherBubble,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        if (!message.isMine) {
            Text(message.senderName, color = senderText, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
        }
        Text(message.text, color = mainText)
        if (showTimestamp) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Text(message.timestamp, color = secondaryText, textAlign = TextAlign.End)
                if (message.isMine) {
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                    Icon(
                        imageVector = if (message.seen) Icons.Default.DoneAll else Icons.Default.Done,
                        contentDescription = null,
                        tint = if (message.seen) Color(0xFF53BDEB) else secondaryText
                    )
                }
            }
        }
    }
}
