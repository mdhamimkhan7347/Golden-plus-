package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CoachingViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = CoachingRepository(db.dao())

    // UI Local State Flows
    val searchQuery = MutableStateFlow("")
    val selectedTab = MutableStateFlow("students") // "students", "teachers", "posts", "inbox", "notices", "payments"
    val paymentFilter = MutableStateFlow("ALL") // "ALL", "PAID", "UNPAID"

    // Exposed Flows from Room
    val allMembers: StateFlow<List<Member>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val teachers: StateFlow<List<Member>> = repository.teachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val students: StateFlow<List<Member>> = repository.students
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPosts: StateFlow<List<Post>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMessages: StateFlow<List<Message>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotices: StateFlow<List<Notice>> = repository.allNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<Payment>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Automatically populates seed data on first installation if empty
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    // CRUD Interactions
    fun addMember(
        name: String,
        role: String,
        phoneNumber: String,
        classOrDesignation: String,
        parentName: String = "",
        parentPhoneNumber: String = "",
        socialAccountUrl: String = "",
        email: String = ""
    ) {
        viewModelScope.launch {
            val memberId = repository.insertMember(
                Member(
                    name = name,
                    role = role,
                    phoneNumber = phoneNumber,
                    classOrDesignation = classOrDesignation,
                    parentName = parentName,
                    parentPhoneNumber = parentPhoneNumber,
                    socialAccountUrl = socialAccountUrl,
                    email = email
                )
            )

            // If it's a student, automatically initialize a payment entry for May 2026 as unpaid
            if (role == "STUDENT") {
                val baseAmount = if (classOrDesignation.contains("10")) 1500.0 else 1200.0
                repository.insertPayment(
                    Payment(
                        studentId = memberId.toInt(),
                        studentName = name,
                        className = classOrDesignation,
                        month = "May 2026",
                        amount = baseAmount,
                        isPaid = false,
                        dueDate = "25 May 2026",
                        remarks = "Pending setup"
                    )
                )
            }
        }
    }

    fun deleteMember(member: Member) {
        viewModelScope.launch {
            repository.deleteMember(member)
        }
    }

    fun addPost(authorName: String, role: String, content: String) {
        viewModelScope.launch {
            repository.insertPost(
                Post(
                    authorName = authorName,
                    authorRole = role,
                    content = content
                )
            )
        }
    }

    fun likePost(postId: Int) {
        viewModelScope.launch {
            repository.likePost(postId)
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            repository.deletePost(post)
        }
    }

    fun addMessage(senderName: String, senderRole: String, content: String) {
        viewModelScope.launch {
            repository.insertMessage(
                Message(
                    senderName = senderName,
                    senderRole = senderRole,
                    content = content
                )
            )
        }
    }

    fun addNotice(title: String, content: String, category: String) {
        viewModelScope.launch {
            repository.insertNotice(
                Notice(
                    title = title,
                    content = content,
                    category = category
                )
            )
        }
    }

    fun deleteNotice(notice: Notice) {
        viewModelScope.launch {
            repository.deleteNotice(notice)
        }
    }

    fun addPayment(
        studentId: Int,
        studentName: String,
        className: String,
        month: String,
        amount: Double,
        isPaid: Boolean,
        dueDate: String,
        remarks: String
    ) {
        viewModelScope.launch {
            repository.insertPayment(
                Payment(
                    studentId = studentId,
                    studentName = studentName,
                    className = className,
                    month = month,
                    amount = amount,
                    isPaid = isPaid,
                    paymentDate = if (isPaid) System.currentTimeMillis() else 0,
                    dueDate = dueDate,
                    remarks = remarks
                )
            )
        }
    }

    fun togglePaymentPaidStatus(payment: Payment) {
        viewModelScope.launch {
            val updated = payment.copy(
                isPaid = !payment.isPaid,
                paymentDate = if (!payment.isPaid) System.currentTimeMillis() else 0
            )
            repository.updatePayment(updated)
        }
    }

    fun updatePaymentDetails(payment: Payment, amount: Double, remarks: String) {
        viewModelScope.launch {
            val updated = payment.copy(
                amount = amount,
                remarks = remarks
            )
            repository.updatePayment(updated)
        }
    }

    fun deletePayment(payment: Payment) {
        viewModelScope.launch {
            repository.deletePayment(payment)
        }
    }
}
