package com.example.setchat.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SetChatDao {
    @Query("SELECT * FROM Contact ORDER BY name ASC")
    fun getContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM Conversation ORDER BY id DESC")
    fun getConversations(): Flow<List<Conversation>>

    @Query("SELECT * FROM Message WHERE conversationId = :conversationId ORDER BY id ASC")
    fun getMessages(conversationId: Long): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message): Long

    @Update
    suspend fun updateConversation(conversation: Conversation)

    @Query("SELECT * FROM Conversation WHERE id = :id LIMIT 1")
    suspend fun getConversationById(id: Long): Conversation?

    @Query("SELECT COUNT(*) FROM Conversation")
    suspend fun countConversations(): Int

    @Query("SELECT COUNT(*) FROM Contact")
    suspend fun countContacts(): Int
}
