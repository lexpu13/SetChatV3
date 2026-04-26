package com.example.setchat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.setchat.data.local.Conversation
import com.example.setchat.ui.components.ConversationRow

private enum class ConversationTab(val label: String) {
    ALL("Toutes"),
    UNREAD("Non lues"),
    FAVORITES("Favoris"),
    GROUP("Groupes"),
    PRIVATE("Persos")
}

@Composable
fun HomeScreen(
    conversations: List<Conversation>,
    onOpenChat: (Long) -> Unit,
    onOpenContacts: () -> Unit,
    onOpenAdmin: () -> Unit,
    isDarkTheme: Boolean = true
) {
    val pageBg = if (isDarkTheme) Color(0xFF050A0E) else Color(0xFFF1F4F8)
    val titleColor = if (isDarkTheme) Color(0xFFEFF6FF) else Color(0xFF102338)
    val iconTint = if (isDarkTheme) Color(0xFFDEE9F5) else Color(0xFF12324A)
    val iconButtonBg = if (isDarkTheme) Color(0xFF1B222A) else Color(0xFFE9EEF4)
    val searchBg = if (isDarkTheme) Color(0xFF1B222A) else Color(0xFFE6EDF4)
    val searchHint = if (isDarkTheme) Color(0xFF9FB0C0) else Color(0xFF5D7387)
    val searchText = if (isDarkTheme) Color(0xFFE8F3FF) else Color(0xFF22384D)
    val tabBg = if (isDarkTheme) Color(0xFF171F27) else Color(0xFFE7EEF5)
    val tabSelected = Color(0xFF2D8CFF)
    val tabUnselectedText = if (isDarkTheme) Color(0xFFB6C5D4) else Color(0xFF4B6479)
    val bottomBarBg = if (isDarkTheme) Color(0xFF1A1F24) else Color(0xFFE8EDF3)
    val bottomIcon = if (isDarkTheme) Color(0xFF8EA5BC) else Color(0xFF698299)
    val bottomLabel = if (isDarkTheme) Color(0xFFA9BACB) else Color(0xFF5C758B)

    var selectedTabName by rememberSaveable { mutableStateOf(ConversationTab.ALL.name) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val selectedTab = remember(selectedTabName) {
        runCatching { ConversationTab.valueOf(selectedTabName) }.getOrDefault(ConversationTab.ALL)
    }

    val filteredConversations = remember(conversations, selectedTab, searchQuery) {
        val tabFiltered = when (selectedTab) {
            ConversationTab.ALL -> conversations
            ConversationTab.PRIVATE -> conversations.filter { it.type != "group" }
            ConversationTab.GROUP -> conversations.filter { it.type == "group" }
            ConversationTab.UNREAD -> conversations.filter { it.unreadCount > 0 }
            ConversationTab.FAVORITES -> conversations.filter { conversation ->
                conversation.sequenceNumber.isNotBlank() ||
                    conversation.title.contains("★") ||
                    conversation.subtitle.contains("⭐")
            }
        }
        val query = searchQuery.trim()
        if (query.isBlank()) {
            tabFiltered
        } else {
            tabFiltered.filter { conversation ->
                conversation.title.contains(query, ignoreCase = true) ||
                    conversation.subtitle.contains(query, ignoreCase = true) ||
                    conversation.lastMessage.contains(query, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBg)
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 34.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = iconButtonBg
            ) {
                IconButton(onClick = onOpenContacts, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "Menu", tint = iconTint)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = iconButtonBg) {
                    IconButton(onClick = onOpenContacts, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = iconTint)
                    }
                }
                Surface(shape = CircleShape, color = Color(0xFF75D47D)) {
                    IconButton(onClick = onOpenAdmin, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter", tint = Color(0xFF0B2D11))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Discussions",
            color = titleColor,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp
        )

        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            color = searchBg,
            shape = RoundedCornerShape(11.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = searchHint,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                androidx.compose.foundation.layout.Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isBlank()) {
                        Text(
                            text = "Demander à Meta AI ou rechercher",
                            color = searchHint,
                            maxLines = 1
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = searchText,
                            fontSize = 15.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(ConversationTab.values().toList()) { tab ->
                FilterChip(
                    selected = selectedTab == tab,
                    onClick = { selectedTabName = tab.name },
                    label = {
                        Text(
                            tab.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = tabSelected,
                        selectedLabelColor = Color(0xFFE9FFF5),
                        containerColor = tabBg,
                        labelColor = tabUnselectedText
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = null,
                tint = if (isDarkTheme) Color(0xFFA5B4C4) else Color(0xFF516A7F),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Archivées",
                color = if (isDarkTheme) Color(0xFF9FB0C0) else Color(0xFF5C758B),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(filteredConversations) { conversation ->
                ConversationRow(
                    item = conversation,
                    onClick = { onOpenChat(conversation.id) },
                    isDarkTheme = isDarkTheme
                )
            }
            if (filteredConversations.isEmpty()) {
                item {
                    Text(
                        text = "Aucune discussion pour ce filtre.",
                        color = tabUnselectedText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 20.dp)
                    )
                }
            }
        }

        Surface(
            color = bottomBarBg,
            shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BottomNavItem(icon = Icons.Default.MoreHoriz, label = "Actus", selected = false, activeColor = tabSelected, inactiveIcon = bottomIcon, inactiveLabel = bottomLabel)
                BottomNavItem(icon = Icons.Default.Call, label = "Appels", selected = false, activeColor = tabSelected, inactiveIcon = bottomIcon, inactiveLabel = bottomLabel)
                BottomNavItem(icon = Icons.Default.Groups, label = "Communautés", selected = false, activeColor = tabSelected, inactiveIcon = bottomIcon, inactiveLabel = bottomLabel)
                BottomNavItem(icon = Icons.Default.Chat, label = "Discussions", selected = true, activeColor = tabSelected, inactiveIcon = bottomIcon, inactiveLabel = bottomLabel)
                BottomNavItem(icon = Icons.Default.Person, label = "Vous", selected = false, activeColor = tabSelected, inactiveIcon = bottomIcon, inactiveLabel = bottomLabel)
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    activeColor: Color,
    inactiveIcon: Color,
    inactiveLabel: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) activeColor else inactiveIcon,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            color = if (selected) Color.White else inactiveLabel,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
