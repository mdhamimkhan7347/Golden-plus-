package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CoachingRepository(private val dao: CoDao) {
    val allMembers: Flow<List<Member>> = dao.getAllMembers()
    val teachers: Flow<List<Member>> = dao.getMembersByRole("TEACHER")
    val students: Flow<List<Member>> = dao.getMembersByRole("STUDENT")
    val allPosts: Flow<List<Post>> = dao.getAllPosts()
    val allMessages: Flow<List<Message>> = dao.getAllMessages()
    val allNotices: Flow<List<Notice>> = dao.getAllNotices()
    val allPayments: Flow<List<Payment>> = dao.getAllPayments()

    suspend fun insertMember(member: Member): Long = dao.insertMember(member)
    suspend fun deleteMember(member: Member) = dao.deleteMember(member)

    suspend fun insertPost(post: Post) = dao.insertPost(post)
    suspend fun likePost(postId: Int) = dao.likePost(postId)
    suspend fun deletePost(post: Post) = dao.deletePost(post)

    suspend fun insertMessage(message: Message) = dao.insertMessage(message)

    suspend fun insertNotice(notice: Notice) = dao.insertNotice(notice)
    suspend fun deleteNotice(notice: Notice) = dao.deleteNotice(notice)

    suspend fun insertPayment(payment: Payment) = dao.insertPayment(payment)
    suspend fun updatePayment(payment: Payment) = dao.updatePayment(payment)
    suspend fun deletePayment(payment: Payment) = dao.deletePayment(payment)

    suspend fun seedDatabaseIfEmpty() {
        val membersList = allMembers.first()
        if (membersList.isEmpty()) {
            val t1Id = dao.insertMember(
                Member(
                    name = "Abdur Rahman",
                    role = "TEACHER",
                    phoneNumber = "01712345678",
                    classOrDesignation = "Physics Head",
                    email = "rahman@example.com",
                    socialAccountUrl = "facebook.com/rahman.phys"
                )
            )

            val t2Id = dao.insertMember(
                Member(
                    name = "Nusrat Jahan",
                    role = "TEACHER",
                    phoneNumber = "01812345678",
                    classOrDesignation = "English Lecturer",
                    email = "nusrat@example.com",
                    socialAccountUrl = "facebook.com/nusrat.eng"
                )
            )

            val t3Id = dao.insertMember(
                Member(
                    name = "Kamal Uddin",
                    role = "TEACHER",
                    phoneNumber = "01912345678",
                    classOrDesignation = "Math Coach",
                    email = "kamal@example.com",
                    socialAccountUrl = "facebook.com/kamal.math"
                )
            )

            val s1Id = dao.insertMember(
                Member(
                    name = "Tanvir Ahmed",
                    role = "STUDENT",
                    phoneNumber = "01512345678",
                    classOrDesignation = "Class 10",
                    parentName = "Rafiqul Islam",
                    parentPhoneNumber = "01798765432",
                    socialAccountUrl = "facebook.com/tanvir.ahmed",
                    email = "tanvir@example.com"
                )
            ).toInt()

            val s2Id = dao.insertMember(
                Member(
                    name = "Sultana Razia",
                    role = "STUDENT",
                    phoneNumber = "01612345678",
                    classOrDesignation = "Class 10",
                    parentName = "Amena Begum",
                    parentPhoneNumber = "01898765432",
                    socialAccountUrl = "facebook.com/razia.sultana",
                    email = "razia@example.com"
                )
            ).toInt()

            val s3Id = dao.insertMember(
                Member(
                    name = "Fahim Shahriar",
                    role = "STUDENT",
                    phoneNumber = "01312345678",
                    classOrDesignation = "Class 9",
                    parentName = "Anwar Hossain",
                    parentPhoneNumber = "01398765432",
                    socialAccountUrl = "facebook.com/fahim.shahriar",
                    email = "fahim@example.com"
                )
            ).toInt()

            dao.insertPost(
                Post(
                    authorName = "Nusrat Jahan (English Lecturer)",
                    authorRole = "TEACHER",
                    content = "সুপ্রিয় শিক্ষার্থীবৃন্দ, ইংরেজি ব্যাকরণের পরবর্তী ক্লাসে আমরা Right Form of Verbs নিয়ে আলোচনা করব। সবাই প্রস্তুতি নিয়ে ক্লাসে আসবে।",
                    likesCount = 14
                )
            )

            dao.insertPost(
                Post(
                    authorName = "Tanvir Ahmed",
                    authorRole = "STUDENT",
                    content = "আজকের পদার্থবিজ্ঞান ক্লাসটা অসাধারণ ছিল! রহমান স্যার অংকগুলো খুব সহজ করে বুঝিয়ে দিয়েছেন। ধন্যবাদ স্যার!",
                    likesCount = 8
                )
            )

            dao.insertPost(
                Post(
                    authorName = "Kamal Uddin (Math Coach)",
                    authorRole = "TEACHER",
                    content = "মনে রাখবে, গণিত অনুশীলনের বিকল্প নেই। পরীক্ষার আগে সব অধ্যায়ের সূত্রগুলো আবার ভালোমত ঝালিয়ে নাও। কোনো সমস্যা থাকলে ইনবক্সে জানাবে।",
                    likesCount = 15
                )
            )

            dao.insertMessage(
                Message(
                    senderName = "Tanvir Ahmed",
                    senderRole = "STUDENT",
                    content = "আসসালামু আলাইকুম স্যার, মে মাসের বৃত্তির পরীক্ষা কয়টা থেকে শুরু হবে?"
                )
            )

            dao.insertMessage(
                Message(
                    senderName = "Kamal Uddin (Math Coach)",
                    senderRole = "TEACHER",
                    content = "ওয়ালাইকুমুস সালাম তানভির। পরীক্ষা সকাল ১০টা থেকে শুরু হবে। ৯:৪৫ এর মধ্যে কেন্দ্রে উপস্থিত থেকো।"
                )
            )

            dao.insertMessage(
                Message(
                    senderName = "Sultana Razia",
                    senderRole = "STUDENT",
                    content = "ম্যাডাম, আমার মে মাসের পেমেন্ট রিসিটটা কি দেওয়া যাবে?"
                )
            )

            dao.insertMessage(
                Message(
                    senderName = "Office Admin",
                    senderRole = "ADMIN",
                    content = "হ্যাঁ সুলতানা, আগামী রবিবার কোচিং সেন্টারের অফিস রুম থেকে তোমার মে মাসের পেমেন্ট স্লিপ সংগ্রহ করে নিও।"
                )
            )

            dao.insertNotice(
                Notice(
                    title = "Class 10 Exams",
                    content = "the semi-annual examinations of Class 10 will commence on May 25, 2026. All students are instructed to collect their admit cards and clear all outstanding dues by May 22, 2026.",
                    category = "EXAM"
                )
            )

            dao.insertNotice(
                Notice(
                    title = "Eid-ul-Azha Holidays",
                    content = "Golden Plus Coaching Center will be closed from June 15 to June 22 on account of the holy Eid-ul-Azha. Regular classes will resume on June 23, 2026.",
                    category = "HOLIDAY"
                )
            )

            dao.insertNotice(
                Notice(
                    title = "Career Seminar on Friday",
                    content = "We are hosting a Special Career Guidance Seminar with University Mentors on Friday, May 22 at 3 PM. Recommended for all Class 9 & Class 10 students.",
                    category = "GENERAL"
                )
            )

            dao.insertPayment(
                Payment(
                    studentId = s1Id,
                    studentName = "Tanvir Ahmed",
                    className = "Class 10",
                    month = "May 2026",
                    amount = 1500.0,
                    isPaid = true,
                    paymentDate = System.currentTimeMillis() - (3 * 24 * 3600 * 1000L),
                    dueDate = "2026-05-15",
                    remarks = "Paid fully in cash"
                )
            )

            dao.insertPayment(
                Payment(
                    studentId = s2Id,
                    studentName = "Sultana Razia",
                    className = "Class 10",
                    month = "May 2026",
                    amount = 1500.0,
                    isPaid = false,
                    paymentDate = 0,
                    dueDate = "25 May 2026",
                    remarks = "Will pay by Sunday"
                )
            )

            dao.insertPayment(
                Payment(
                    studentId = s3Id,
                    studentName = "Fahim Shahriar",
                    className = "Class 9",
                    month = "May 2026",
                    amount = 1200.0,
                    isPaid = false,
                    paymentDate = 0,
                    dueDate = "30 May 2026",
                    remarks = "Father requested delay extension"
                )
            )
        }

        // Ensure MD Robin Uddin exists in the database
        val currentMembers = allMembers.first()
        val hasRobin = currentMembers.any { it.name.equals("MD Robin Uddin", ignoreCase = true) }
        if (!hasRobin) {
            dao.insertMember(
                Member(
                    name = "MD Robin Uddin",
                    role = "TEACHER",
                    phoneNumber = "01712344321",
                    classOrDesignation = "ICT Head",
                    email = "robin@example.com",
                    socialAccountUrl = "facebook.com/robin.ict"
                )
            )
        }
    }
}
