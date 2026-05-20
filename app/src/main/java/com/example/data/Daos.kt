package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CoDao {
    // Member queries
    @Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE role = :role ORDER BY name ASC")
    fun getMembersByRole(role: String): Flow<List<Member>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member) : Long

    @Delete
    suspend fun deleteMember(member: Member)

    // Post queries
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Query("UPDATE posts SET likesCount = likesCount + 1 WHERE id = :postId")
    suspend fun likePost(postId: Int)

    @Delete
    suspend fun deletePost(post: Post)

    // Message queries
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    // Notice queries
    @Query("SELECT * FROM notices ORDER BY timestamp DESC")
    fun getAllNotices(): Flow<List<Notice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: Notice)

    @Delete
    suspend fun deleteNotice(notice: Notice)

    // Payment queries
    @Query("SELECT * FROM payments ORDER BY isPaid ASC, id DESC")
    fun getAllPayments(): Flow<List<Payment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment)

    @Update
    suspend fun updatePayment(payment: Payment)

    @Delete
    suspend fun deletePayment(payment: Payment)
}
