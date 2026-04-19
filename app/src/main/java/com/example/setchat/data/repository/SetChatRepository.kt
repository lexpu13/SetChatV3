package com.example.setchat.data.repository

import com.example.setchat.data.local.Contact
import com.example.setchat.data.local.Conversation
import com.example.setchat.data.local.Message
import com.example.setchat.data.local.SetChatDao
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SetChatRepository(private val dao: SetChatDao) {
    fun contacts() = dao.getContacts()
    fun conversations() = dao.getConversations()
    fun messages(conversationId: Long) = dao.getMessages(conversationId)

    suspend fun addContact(name: String, role: String, status: String) {
        if (name.isBlank()) return
        dao.insertContact(
            Contact(
                name = name.trim(),
                role = role.trim(),
                status = status.trim(),
                avatarType = "initials",
                avatarText = initialsFor(name),
                avatarUri = ""
            )
        )
    }

    suspend fun addConversation(title: String, isGroup: Boolean) {
        if (title.isBlank()) return
        dao.insertConversation(
            Conversation(
                title = title.trim(),
                subtitle = if (isGroup) "4 participants actifs" else "vu récemment",
                type = if (isGroup) "group" else "private"
            )
        )
    }

    suspend fun sendMessage(conversationId: Long, text: String, isMine: Boolean, senderName: String) {
        sendMessage(
            conversationId = conversationId,
            text = text,
            isMine = isMine,
            senderName = senderName,
            incrementUnread = !isMine
        )
    }

    suspend fun sendMessage(
        conversationId: Long,
        text: String,
        isMine: Boolean,
        senderName: String,
        incrementUnread: Boolean
    ) {
        if (text.isBlank()) return
        val time = now()
        dao.insertMessage(
            Message(
                conversationId = conversationId,
                senderName = senderName,
                text = text.trim(),
                timestamp = time,
                isMine = isMine,
                delivered = true,
                seen = isMine
            )
        )
        val c = dao.getConversationById(conversationId) ?: return
        dao.updateConversation(
            c.copy(
                lastMessage = text.trim(),
                lastTime = time,
                unreadCount = if (incrementUnread) c.unreadCount + 1 else 0
            )
        )
    }

    suspend fun markConversationRead(conversationId: Long) {
        val conversation = dao.getConversationById(conversationId) ?: return
        if (conversation.unreadCount == 0) return
        dao.updateConversation(conversation.copy(unreadCount = 0))
    }

    suspend fun seedIfNeeded() {
        if (dao.countContacts() > 0 || dao.countConversations() > 0) return

        dao.insertContact(Contact(name = "Emma", role = "Designer", status = "En ligne", avatarType = "initials", avatarText = "EM"))
        dao.insertContact(Contact(name = "Lucas", role = "Dev mobile", status = "Tape un message…", avatarType = "initials", avatarText = "LU"))
        dao.insertContact(Contact(name = "Nina", role = "Marketing", status = "Disponible", avatarType = "initials", avatarText = "NI"))
        dao.insertContact(Contact(name = "Yanis", role = "Freelance", status = "Vu il y a 5 min", avatarType = "initials", avatarText = "YA"))
        dao.insertContact(Contact(name = "Sarah", role = "Cheffe de projet", status = "En réunion", avatarType = "initials", avatarText = "SA"))

        val c1 = dao.insertConversation(
            Conversation(
                title = "Emma",
                subtitle = "en ligne",
                unreadCount = 1,
                lastMessage = "Tu peux m'envoyer la maquette ?",
                lastTime = "08:12",
                type = "private"
            )
        )
        val c2 = dao.insertConversation(
            Conversation(
                title = "Team Android",
                subtitle = "Lucas, Sarah, Nina",
                unreadCount = 2,
                lastMessage = "On valide la démo ce soir ?",
                lastTime = "09:21",
                type = "group"
            )
        )
        val c3 = dao.insertConversation(
            Conversation(
                title = "Yanis",
                subtitle = "vu récemment",
                unreadCount = 0,
                lastMessage = "Je t'appelle dans 10 min",
                lastTime = "09:02",
                type = "private"
            )
        )

        dao.insertMessage(Message(conversationId = c1, senderName = "Emma", text = "Tu peux m'envoyer la maquette ?", timestamp = "08:12", isMine = false))
        dao.insertMessage(Message(conversationId = c1, senderName = "Moi", text = "Oui, je te la partage ce matin.", timestamp = "08:13", isMine = true, seen = true))
        dao.insertMessage(Message(conversationId = c1, senderName = "Emma", text = "Parfait, merci.", timestamp = "08:14", isMine = false))

        dao.insertMessage(Message(conversationId = c2, senderName = "Lucas", text = "On valide la démo ce soir ?", timestamp = "09:20", isMine = false))
        dao.insertMessage(Message(conversationId = c2, senderName = "Sarah", text = "Oui, il faut une version stable avant 18h.", timestamp = "09:21", isMine = false))
        dao.insertMessage(Message(conversationId = c2, senderName = "Moi", text = "Je pousse une build dans l'après-midi.", timestamp = "09:22", isMine = true, seen = true))

        dao.insertMessage(Message(conversationId = c3, senderName = "Yanis", text = "Je t'appelle dans 10 min", timestamp = "09:02", isMine = false))
        dao.insertMessage(Message(conversationId = c3, senderName = "Moi", text = "Ça marche, je reste dispo.", timestamp = "09:03", isMine = true, seen = true))
    }

    private fun now(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    private fun initialsFor(name: String): String {
        return name
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.take(1).uppercase(Locale.getDefault()) }
            .ifBlank { "SC" }
    }
}
