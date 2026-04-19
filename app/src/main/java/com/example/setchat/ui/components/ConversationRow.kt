package com.example.setchat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.setchat.data.local.Conversation

@Composable
fun ConversationRow(
    item: Conversation,
    onClick: () -> Unit,
    isDarkTheme: Boolean = true
) {
    val rowColor = if (isDarkTheme) Color(0xFF111B21) else Color.White
    val primaryText = if (isDarkTheme) Color(0xFFEAF2FF) else Color(0xFF111B21)
    val secondaryText = if (isDarkTheme) Color(0xFF8EA5BC) else Color(0xFF667781)
    val subtitleText = if (isDarkTheme) Color(0xFF62B0FF) else Color(0xFF0B8F68)
    val unreadBadge = if (isDarkTheme) Color(0xFF1F7AE0) else Color(0xFF25D366)

    Surface(
        onClick = onClick,
        color = rowColor,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarCircle(if (item.type == "group") "👥" else item.title.take(1).uppercase())
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.title, fontWeight = FontWeight.SemiBold, color = primaryText)
                    Text(item.lastTime, color = secondaryText)
                }
                if (item.subtitle.isNotBlank()) {
                    Text(item.subtitle, color = subtitleText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.lastMessage.ifBlank { item.subtitle.ifBlank { "Aucun message" } },
                        color = secondaryText,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (item.unreadCount > 0) {
                        Surface(color = unreadBadge, shape = RoundedCornerShape(20.dp)) {
                            Text(
                                text = item.unreadCount.toString(),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
