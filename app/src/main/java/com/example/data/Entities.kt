package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val role: String, // "STUDENT" or "TEACHER"
    val phoneNumber: String,
    val classOrDesignation: String, // e.g., "Class 10", "Maths Head"
    val parentName: String = "", // empty for teachers
    val parentPhoneNumber: String = "", // empty for teachers
    val socialAccountUrl: String = "", // URL or username (e.g. fb.com/xyz)
    val email: String = "",
    val joiningDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val authorRole: String, // "TEACHER", "STUDENT", "ADMIN"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderName: String,
    val senderRole: String, // "TEACHER", "STUDENT", "ADMIN"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notices")
data class Notice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: String, // "EXAM", "HOLIDAY", "GENERAL"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int, // Set to Member's ID or 0 if custom
    val studentName: String,
    val className: String, // e.g. "Class 10"
    val month: String, // e.g., "May 2026"
    val amount: Double,
    val isPaid: Boolean,
    val paymentDate: Long = 0, // 0 if unpaid
    val dueDate: String = "", // e.g., "2026-05-25"
    val remarks: String = ""
)
