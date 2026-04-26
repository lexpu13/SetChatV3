package com.example.setchat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.setchat.data.local.Message

@Composable
fun ChatBubble(
    modifier: Modifier = Modifier,
    message: Message,
    isDarkTheme: Boolean = true,
    showTimestamp: Boolean = true,
    showSenderName: Boolean = true,
    showDeliveryIcon: Boolean = false
) {
    val mineBubble = if (isDarkTheme) Color(0xFF134B78) else Color(0xFFD9FDD3)
    val otherBubble = if (isDarkTheme) Color(0xFF1D232A) else Color.White
    val mainText = if (isDarkTheme) Color(0xFFF2F7FD) else Color(0xFF111B21)
    val secondaryText = if (isDarkTheme) Color(0xFF8EA2B6) else Color(0xFF667781)
    val senderText = if (isDarkTheme) Color(0xFF5FB4FF) else Color(0xFF0B8F68)
    var renderedLineCount by remember(message.id, message.text) { mutableIntStateOf(1) }
    val compactBubble = renderedLineCount <= 1
    val bubbleVerticalPadding = if (compactBubble) 4.dp else 8.dp
    val bubbleHorizontalPadding = if (compactBubble) 8.dp else 11.dp

    Column(
        modifier = Modifier
            .then(modifier)
            .background(
                color = if (message.isMine) mineBubble else otherBubble,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = bubbleHorizontalPadding, vertical = bubbleVerticalPadding)
    ) {
        if (!message.isMine && showSenderName) {
            Text(
                text = "∼${message.senderName}",
                color = senderText,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        if (message.text.isNotBlank()) {
            if (compactBubble && showTimestamp && message.timestamp.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = message.text,
                        color = mainText,
                        fontSize = 17.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                        onTextLayout = { textLayoutResult ->
                            renderedLineCount = textLayoutResult.lineCount.coerceAtLeast(1)
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = message.timestamp,
                        color = secondaryText,
                        fontSize = 12.sp
                    )
                }
            } else {
                Text(
                    text = message.text,
                    color = mainText,
                    fontSize = 17.sp,
                    textAlign = if (compactBubble) androidx.compose.ui.text.style.TextAlign.Center else androidx.compose.ui.text.style.TextAlign.Start,
                    modifier = if (compactBubble) Modifier.align(Alignment.CenterHorizontally) else Modifier,
                    onTextLayout = { textLayoutResult ->
                        renderedLineCount = textLayoutResult.lineCount.coerceAtLeast(1)
                    }
                )
            }
        }

        if (showTimestamp && message.timestamp.isNotBlank() && !compactBubble) {
            Spacer(modifier = Modifier.height(if (compactBubble) 1.dp else 3.dp))
            Box(modifier = Modifier.align(Alignment.End)) {
                Text(
                    text = message.timestamp,
                    color = secondaryText,
                    fontSize = 12.sp
                )
            }
        }

        if (showDeliveryIcon) {
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}
