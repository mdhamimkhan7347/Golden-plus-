package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.Member
import com.example.data.Payment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        role: String,
        phone: String,
        classOrDesignation: String,
        parentName: String,
        parentPhone: String,
        social: String,
        email: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("STUDENT") } // "STUDENT" or "TEACHER"
    var phone by remember { mutableStateOf("") }
    var classOrDesignation by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var parentPhone by remember { mutableStateOf("") }
    var social by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন সদস্য যোগ করুন", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Role Selection
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        Modifier
                            .selectable(
                                selected = (role == "STUDENT"),
                                onClick = { role = "STUDENT" }
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (role == "STUDENT"),
                            onClick = { role = "STUDENT" },
                            modifier = Modifier.testTag("role_student_radio")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("শিক্ষার্থী (Student)")
                    }

                    Row(
                        Modifier
                            .selectable(
                                selected = (role == "TEACHER"),
                                onClick = { role = "TEACHER" }
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (role == "TEACHER"),
                            onClick = { role = "TEACHER" },
                            modifier = Modifier.testTag("role_teacher_radio")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("শিক্ষক (Teacher)")
                    }
                }

                // Input Fields
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("নাম (Name) *") },
                    modifier = Modifier.fillMaxWidth().testTag("member_name_input"),
                    isError = hasError && name.isEmpty()
                )

                OutlinedTextField(
                    value = classOrDesignation,
                    onValueChange = { classOrDesignation = it },
                    label = { Text(if (role == "STUDENT") "শ্রেণী / ব্যাচ (যেমন: Class 10) *" else "বিষয় / পদবী (যেমন: Physics) *") },
                    modifier = Modifier.fillMaxWidth().testTag("member_designation_input"),
                    isError = hasError && classOrDesignation.isEmpty()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নাম্বার *") },
                    modifier = Modifier.fillMaxWidth().testTag("member_phone_input"),
                    isError = hasError && phone.isEmpty()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("ইমেইল (ঐচ্ছিক)") },
                    modifier = Modifier.fillMaxWidth().testTag("member_email_input")
                )

                OutlinedTextField(
                    value = social,
                    onValueChange = { social = it },
                    label = { Text("সোশ্যাল লিংক / প্রোফাইল (ঐচ্ছিক)") },
                    placeholder = { Text("facebook.com/username") },
                    modifier = Modifier.fillMaxWidth().testTag("member_social_input")
                )

                if (role == "STUDENT") {
                    Text("পিতা/মাতার সমস্ত তথ্য (শুধুমাত্র শিক্ষার্থীর জন্য):", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = parentName,
                        onValueChange = { parentName = it },
                        label = { Text("পিতার/মাতার নাম") },
                        modifier = Modifier.fillMaxWidth().testTag("parent_name_input")
                    )

                    OutlinedTextField(
                        value = parentPhone,
                        onValueChange = { parentPhone = it },
                        label = { Text("পিতা/মাতার মোবাইল নাম্বার") },
                        modifier = Modifier.fillMaxWidth().testTag("parent_phone_input")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && phone.isNotEmpty() && classOrDesignation.isNotEmpty()) {
                        onConfirm(name, role, phone, classOrDesignation, parentName, parentPhone, social, email)
                    } else {
                        hasError = true
                    }
                },
                modifier = Modifier.testTag("submit_member_button")
            ) {
                Text("যোগ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@Composable
fun AddPostDialog(
    onDismiss: () -> Unit,
    onConfirm: (author: String, role: String, content: String) -> Unit
) {
    var author by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("ADMIN") } // "TEACHER", "STUDENT", "ADMIN"
    var content by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("সোশ্যাল ওয়ালে পোস্ট করুন", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("পোস্টদাতার নাম (Author) *") },
                    placeholder = { Text("যেমন: কামাল উদ্দিন স্যার / তানভির") },
                    modifier = Modifier.fillMaxWidth().testTag("post_author_input"),
                    isError = hasError && author.isEmpty()
                )

                // Role Segmented selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ভূমিকা (Role):", style = MaterialTheme.typography.bodyMedium)
                    Row {
                        FilterChip(
                            selected = role == "TEACHER",
                            onClick = { role = "TEACHER" },
                            label = { Text("শিক্ষক") }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(
                            selected = role == "STUDENT",
                            onClick = { role = "STUDENT" },
                            label = { Text("শিক্ষার্থী") }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(
                            selected = role == "ADMIN",
                            onClick = { role = "ADMIN" },
                            label = { Text("এডমিন") }
                        )
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("পোস্টের বিষয়বস্তু *") },
                    placeholder = { Text("এখানে আপনার বার্তা লিখুন...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("post_content_input"),
                    isError = hasError && content.isEmpty()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (author.isNotEmpty() && content.isNotEmpty()) {
                        onConfirm(author, role, content)
                    } else {
                        hasError = true
                    }
                },
                modifier = Modifier.testTag("submit_post_button")
            ) {
                Text("পোস্ট")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@Composable
fun AddNoticeDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("GENERAL") } // "EXAM", "HOLIDAY", "GENERAL"
    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন গুরুত্বপূর্ণ নোটিশ বোর্ড পোস্ট", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("নোটিশের শিরোনাম *") },
                    modifier = Modifier.fillMaxWidth().testTag("notice_title_input"),
                    isError = hasError && title.isEmpty()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ক্যাটাগরি:", style = MaterialTheme.typography.bodyMedium)
                    Row {
                        FilterChip(
                            selected = category == "EXAM",
                            onClick = { category = "EXAM" },
                            label = { Text("পরীক্ষা") }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(
                            selected = category == "HOLIDAY",
                            onClick = { category = "HOLIDAY" },
                            label = { Text("ছুটি") }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(
                            selected = category == "GENERAL",
                            onClick = { category = "GENERAL" },
                            label = { Text("সাধারণ") }
                        )
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("বিস্তারিত বিবরণ *") },
                    placeholder = { Text("নোটিশের বিস্তারিত বার্তা এখানে লিখুন...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("notice_content_input"),
                    isError = hasError && content.isEmpty()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && content.isNotEmpty()) {
                        onConfirm(title, content, category)
                    } else {
                        hasError = true
                    }
                },
                modifier = Modifier.testTag("submit_notice_button")
            ) {
                Text("নোটিশ প্রকাশ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@Composable
fun AddPaymentDialog(
    students: List<com.example.data.Member>,
    onDismiss: () -> Unit,
    onConfirm: (
        studentId: Int,
        studentName: String,
        className: String,
        month: String,
        amount: Double,
        isPaid: Boolean,
        dueDate: String,
        remarks: String
    ) -> Unit
) {
    var studentId by remember { mutableStateOf(0) }
    var studentName by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("Class 10") }
    var month by remember { mutableStateOf("May 2026") }
    var amountText by remember { mutableStateOf("1500") }
    var isPaid by remember { mutableStateOf(false) }
    var dueDate by remember { mutableStateOf("25 May 2026") }
    var remarks by remember { mutableStateOf("") }

    var expandedStudents by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    // Pre-select first student if available
    LaunchedEffect(students) {
        if (students.isNotEmpty()) {
            studentId = students[0].id
            studentName = students[0].name
            className = students[0].classOrDesignation
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন পেমেন্ট বিল যোগ করুন", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (students.isEmpty()) {
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("শিক্ষার্থীর নাম *") },
                        modifier = Modifier.fillMaxWidth().testTag("payment_manual_student"),
                        isError = hasError && studentName.isEmpty()
                    )
                } else {
                    Text("শিক্ষার্থী নির্বাচন করুন:", style = MaterialTheme.typography.bodyMedium)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { expandedStudents = true },
                            modifier = Modifier.fillMaxWidth().testTag("payment_select_student")
                        ) {
                            Text(if (studentName.isEmpty()) "নির্বাচন করুন" else "$studentName ($className)")
                        }
                        DropdownMenu(
                            expanded = expandedStudents,
                            onDismissRequest = { expandedStudents = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            students.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text("${s.name} (${s.classOrDesignation})") },
                                    onClick = {
                                        studentId = s.id
                                        studentName = s.name
                                        className = s.classOrDesignation
                                        expandedStudents = false
                                        // Default amount
                                        amountText = if (className.contains("10")) "1500" else "1200"
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = className,
                    onValueChange = { className = it },
                    label = { Text("শ্রেণী/ব্যাচ *") },
                    modifier = Modifier.fillMaxWidth().testTag("payment_class_input")
                )

                OutlinedTextField(
                    value = month,
                    onValueChange = { month = it },
                    label = { Text("মাসের নাম (যেমন: May 2026) *") },
                    modifier = Modifier.fillMaxWidth().testTag("payment_month_input")
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("বেতনের পরিমাণ (টাকা) *") },
                    modifier = Modifier.fillMaxWidth().testTag("payment_amount_input"),
                    isError = hasError && amountText.toDoubleOrNull() == null
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isPaid,
                        onCheckedChange = { isPaid = it },
                        modifier = Modifier.testTag("payment_ispaid_check")
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ইতোমধ্যে পরিশোধিত (Already Paid)")
                }

                if (!isPaid) {
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("পরিশোধের শেষ তারিখ (Due Date)") },
                        modifier = Modifier.fillMaxWidth().testTag("payment_due_input")
                    )
                }

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("মন্তব্য / নোটস (রিমার্কস)") },
                    placeholder = { Text("যেমন: রবিবারে দেবে / বিকাশ পেমেন্ট") },
                    modifier = Modifier.fillMaxWidth().testTag("payment_remarks_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull()
                    if (studentName.isNotEmpty() && amt != null) {
                        onConfirm(studentId, studentName, className, month, amt, isPaid, dueDate, remarks)
                    } else {
                        hasError = true
                    }
                },
                modifier = Modifier.testTag("submit_payment_button")
            ) {
                Text("বিল তৈরি করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@Composable
fun EditPaymentDialog(
    payment: Payment,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, remarks: String) -> Unit
) {
    var amountText by remember { mutableStateOf(payment.amount.toInt().toString()) }
    var remarks by remember { mutableStateOf(payment.remarks) }
    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("পেমেন্ট তথ্য পরিবর্তন", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("শিক্ষার্থী: ${payment.studentName}", style = MaterialTheme.typography.bodyLarge)
                Text("মাস: ${payment.month}", style = MaterialTheme.typography.bodyMedium)

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("নতুন বেতনের পরিমাণ (টাকা)") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_payment_amount"),
                    isError = hasError && amountText.toDoubleOrNull() == null
                )

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("রিমার্কস (মন্তব্য)") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_payment_remarks")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull()
                    if (amt != null) {
                        onConfirm(amt, remarks)
                    } else {
                        hasError = true
                    }
                },
                modifier = Modifier.testTag("submit_edit_payment_button")
            ) {
                Text("আপডেট করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

private fun valFromRole(role: String, s1: String, s2: String): String {
    return "" // Used as variable proxy
}
