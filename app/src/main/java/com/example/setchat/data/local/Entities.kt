package com.example.setchat.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val role: String = "",
    val status: String = "",
    val avatarType: String = "initials",
    val avatarText: String = "SC",
    val avatarUri: String = "",
    val avatarColor: Long = 0xFF1F7AE0
)

@Entity
data class Conversation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String = "",
    val sequenceNumber: String = "",
    val avatarUri: String = "",
    val videoCallBackgroundUri: String = "",
    val showMessageTimes: Boolean = true,
    val type: String = "private",
    val unreadCount: Int = 0,
    val lastMessage: String = "",
    val lastMessageIsMine: Boolean = false,
    val lastMessageSeen: Boolean = false,
    val lastTime: String = "09:41"
)

@Entity(primaryKeys = ["conversationId", "contactId"])
data class ConversationParticipant(
    val conversationId: Long,
    val contactId: Long
)

@Entity
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val kind: String = "message",
    val sortOrder: Long = 0,
    val senderName: String,
    val text: String,
    val imageUri: String = "",
    val timestamp: String,
    val isMine: Boolean = false,
    val delivered: Boolean = true,
    val seen: Boolean = false
)

@Entity
data class GalleryImage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uri: String,
    val mediaType: String = "image",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity
data class NotificationPreset(
    @PrimaryKey val id: Long,
    val label: String = "",
    val conversationId: Long = 0L,
    val senderContactId: Long = 0L,
    val messageText: String = "",
    val previewMode: String = "full",
    val previewLength: Int = 42,
    val useProfilePhoto: Boolean = false,
    val durationSeconds: Int = 4,
    val triggerKey: String = ""
)

@Entity
data class AppUiSettings(
    @PrimaryKey val id: Long = 1L,
    val isDarkTheme: Boolean = true,
    val statusBarTime: String = "21:18",
    val statusBarOperator: String = "Operateur",
    val statusBarNetwork: String = "5G",
    val statusBarBatteryLevel: Int = 58,
    val lockWallpaperUri: String = "",
    val homeWallpaperUri: String = "",
    val lockTime: String = "19:42",
    val showLockTime: Boolean = true,
    val lockDate: String = "vendredi 12 avril",
    val showLockDate: Boolean = true,
    val exportEnabled: Boolean = false,
    val exportFormat: String = "pdf"
)
