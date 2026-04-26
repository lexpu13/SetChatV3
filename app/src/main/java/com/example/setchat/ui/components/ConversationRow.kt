package com.example.setchat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.setchat.data.local.Conversation

@Composable
fun ConversationRow(
    item: Conversation,
    onClick: () -> Unit,
    isDarkTheme: Boolean = true
) {
    val rowColor = Color.Transparent
    val primaryText = if (isDarkTheme) Color(0xFFEAF2FF) else Color(0xFF111B21)
    val secondaryText = if (isDarkTheme) Color(0xFFA8B8C8) else Color(0xFF667781)
    val previewTextColor = if (isDarkTheme) Color(0xFFB2C2D1) else Color(0xFF6D7F90)
    val readMarkColor = if (isDarkTheme) Color(0xFF52A7FF) else Color(0xFF1F7AE0)
    val unreadBadge = if (isDarkTheme) Color(0xFF28B66E) else Color(0xFF25D366)
    val divider = if (isDarkTheme) Color(0xFF0E1A24) else Color(0xFFE3E8EE)

    val content = item.lastMessage.ifBlank { "Aucun message" }
    val preview = if (item.lastMessageIsMine) {
        "✓✓ Vous: $content"
    } else {
        val senderPrefix = if (item.type == "group" && item.subtitle.isNotBlank()) {
            "${item.subtitle.substringBefore(",")}: "
        } else {
            ""
        }
        senderPrefix + content
    }

    Surface(
        onClick = onClick,
        color = rowColor,
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarCircle(
                    label = if (item.type == "group") "👥" else item.title.take(1).uppercase(),
                    imageUri = item.avatarUri,
                    size = 50.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            item.title,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(item.lastTime, color = secondaryText)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            preview,
                            color = if (item.lastMessageIsMine) readMarkColor else previewTextColor,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (item.unreadCount > 0) {
                            Surface(
                                color = unreadBadge,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = item.unreadCount.toString(),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
            HorizontalDivider(color = divider, thickness = 1.dp)
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}
