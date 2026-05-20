package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Member
import com.example.data.Notice
import com.example.data.Payment
import com.example.data.Post
import com.example.ui.components.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CoachingViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private val viewModel: CoachingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: CoachingViewModel) {
    val context = LocalContext.current

    // Observe Room and UI states
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val paymentFilter by viewModel.paymentFilter.collectAsStateWithLifecycle()

    val students by viewModel.students.collectAsStateWithLifecycle()
    val teachers by viewModel.teachers.collectAsStateWithLifecycle()
    val posts by viewModel.allPosts.collectAsStateWithLifecycle()
    val messages by viewModel.allMessages.collectAsStateWithLifecycle()
    val notices by viewModel.allNotices.collectAsStateWithLifecycle()
    val payments by viewModel.allPayments.collectAsStateWithLifecycle()

    // Sub-tab specific state for Directory
    var directorySubTab by remember { mutableStateOf("students") } // "students" or "teachers"

    // Dialog state variables
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showAddPostDialog by remember { mutableStateOf(false) }
    var showAddNoticeDialog by remember { mutableStateOf(false) }
    var showAddPaymentDialog by remember { mutableStateOf(false) }
    var paymentToEdit by remember { mutableStateOf<Payment?>(null) }

    // Quick stats derived from states
    val totalStudentsCount = students.size
    val totalTeachersCount = teachers.size
    val activeNoticesCount = notices.size

    val paidPayments = payments.filter { it.isPaid }
    val unpaidPayments = payments.filter { !it.isPaid }
    val totalCollectedAmount = paidPayments.sumOf { it.amount }
    val totalOutstandingAmount = unpaidPayments.sumOf { it.amount }
    val collectionRate = if (payments.isNotEmpty()) {
        (paidPayments.size.toFloat() / payments.size.toFloat() * 100).toInt()
    } else 0

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        topBar = {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.95f)
                            )
                        )
                    )
                    .statusBarsPadding()
            ) {
                // Brand Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageOf(selectedTab),
                                contentDescription = "Tab Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("app_brand_logo")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Golden Plus",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("app_title")
                            )
                        }
                        Text(
                            text = "গোল্ডেন প্লাস কোচিং সেন্টার — শিক্ষা যেখানে সার্থকতা",
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Simple theme quick-pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "এডমিন প্যানেল",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Dashboard KPI Board
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiCard(
                        title = "শিক্ষার্থী",
                        value = "$totalStudentsCount জন",
                        icon = Icons.Default.School,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "শিক্ষক",
                        value = "$totalTeachersCount জন",
                        icon = Icons.Default.Person,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "পেমেন্ট রেট",
                        value = "$collectionRate%",
                        icon = Icons.Default.TrendingUp,
                        modifier = Modifier.weight(1.2f)
                    )
                }

                // Search Box inside header (only visible for searchable screens)
                if (selectedTab == "directory" || selectedTab == "payments" || selectedTab == "notices") {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("global_search_input"),
                        placeholder = { Text("খুঁজুন (যেমন: তানভির, Class 10...)", color = Color.Gray, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = Color.Gray) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Color.Gray)
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp)
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("app_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.secondary,
                tonalElevation = 8.dp
            ) {
                val tabs = listOf(
                    NavigationTab("directory", "ডিরেক্টরি", Icons.Default.People),
                    NavigationTab("posts", "আপডেটস", Icons.Default.Public),
                    NavigationTab("inbox", "ইনবক্স", Icons.Default.Forum),
                    NavigationTab("notices", "নোটিশ board", Icons.Default.Announcement),
                    NavigationTab("payments", "বেতন", Icons.Default.Paid)
                )

                tabs.forEach { tab ->
                    val isActive = selectedTab == tab.id
                    NavigationBarItem(
                        selected = isActive,
                        onClick = {
                            viewModel.selectedTab.value = tab.id
                            viewModel.searchQuery.value = "" // Reset search scope
                        },
                        icon = {
                            Icon(
                                tab.icon,
                                contentDescription = tab.label,
                                tint = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 11.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.id}")
                    )
                }
            }
        },
        floatingActionButton = {
            // Contextual FAB depending on active tab
            when (selectedTab) {
                "directory" -> {
                    FloatingActionButton(
                        onClick = { showAddMemberDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black,
                        modifier = Modifier.testTag("fab_add_member")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Member")
                    }
                }
                "posts" -> {
                    FloatingActionButton(
                        onClick = { showAddPostDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black,
                        modifier = Modifier.testTag("fab_add_post")
                    ) {
                        Icon(Icons.Default.AddComment, contentDescription = "Add Post")
                    }
                }
                "notices" -> {
                    FloatingActionButton(
                        onClick = { showAddNoticeDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black,
                        modifier = Modifier.testTag("fab_add_notice")
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = "Add Notice")
                    }
                }
                "payments" -> {
                    FloatingActionButton(
                        onClick = { showAddPaymentDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black,
                        modifier = Modifier.testTag("fab_add_payment")
                    ) {
                        Icon(Icons.Default.AddCard, contentDescription = "Add Bill")
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Main Switchboard rendering
            when (selectedTab) {
                "directory" -> {
                    DirectoryScreenContent(
                        students = students,
                        teachers = teachers,
                        searchQuery = searchQuery,
                        subTab = directorySubTab,
                        onSubTabChanged = { directorySubTab = it },
                        onDeleteMember = { viewModel.deleteMember(it) }
                    )
                }
                "posts" -> {
                    PostsFeedScreenContent(
                        posts = posts,
                        onLikePost = { viewModel.likePost(it) },
                        onDeletePost = { viewModel.deletePost(it) }
                    )
                }
                "inbox" -> {
                    InboxChatScreenContent(
                        messages = messages,
                        students = students,
                        teachers = teachers,
                        onSendMessage = { sender, role, text ->
                            viewModel.addMessage(sender, role, text)
                        }
                    )
                }
                "notices" -> {
                    NoticesScreenContent(
                        notices = notices,
                        searchQuery = searchQuery,
                        onDeleteNotice = { viewModel.deleteNotice(it) }
                    )
                }
                "payments" -> {
                    PaymentsTrackerScreenContent(
                        payments = payments,
                        searchQuery = searchQuery,
                        filterState = paymentFilter,
                        totalCollected = totalCollectedAmount,
                        totalOutstanding = totalOutstandingAmount,
                        paidCount = paidPayments.size,
                        totalCount = payments.size,
                        onFilterChanged = { viewModel.paymentFilter.value = it },
                        onTogglePaid = { viewModel.togglePaymentPaidStatus(it) },
                        onEditClicked = { paymentToEdit = it },
                        onDeletePayment = { viewModel.deletePayment(it) }
                    )
                }
            }
        }
    }

    // Modal dialog views mapping
    if (showAddMemberDialog) {
        AddMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onConfirm = { name, role, phone, designation, parentName, parentPhone, social, email ->
                viewModel.addMember(name, role, phone, designation, parentName, parentPhone, social, email)
                showAddMemberDialog = false
                Toast.makeText(context, "$name রেজিস্টার্ড হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddPostDialog) {
        AddPostDialog(
            onDismiss = { showAddPostDialog = false },
            onConfirm = { author, role, content ->
                viewModel.addPost(author, role, content)
                showAddPostDialog = false
                Toast.makeText(context, "পোস্ট দেওয়া হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddNoticeDialog) {
        AddNoticeDialog(
            onDismiss = { showAddNoticeDialog = false },
            onConfirm = { title, content, category ->
                viewModel.addNotice(title, content, category)
                showAddNoticeDialog = false
                Toast.makeText(context, "নতুন নোটিশ বোর্ড আপডেট দেওয়া হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddPaymentDialog) {
        AddPaymentDialog(
            students = students,
            onDismiss = { showAddPaymentDialog = false },
            onConfirm = { studentId, studentName, className, month, amount, isPaid, dueDate, remarks ->
                viewModel.addPayment(studentId, studentName, className, month, amount, isPaid, dueDate, remarks)
                showAddPaymentDialog = false
                Toast.makeText(context, "$studentName এর মোবাইল বিল সংযোগ করা হলো", Toast.LENGTH_SHORT).show()
            }
        )
    }

    paymentToEdit?.let { payment ->
        EditPaymentDialog(
            payment = payment,
            onDismiss = { paymentToEdit = null },
            onConfirm = { amount, remarks ->
                viewModel.updatePaymentDetails(payment, amount, remarks)
                paymentToEdit = null
                Toast.makeText(context, "বিল বিবরণ আপডেট করা হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ==================== DESIGN UI MODULE COMPONENTS ====================

data class NavigationTab(val id: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun imageOf(tabId: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (tabId) {
        "directory" -> Icons.Default.People
        "posts" -> Icons.Default.Public
        "inbox" -> Icons.Default.Forum
        "notices" -> Icons.Default.Announcement
        "payments" -> Icons.Default.Paid
        else -> Icons.Default.Star
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = title,
                    color = Color.LightGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// 👥 Directory Panel Component
@Composable
fun DirectoryScreenContent(
    students: List<Member>,
    teachers: List<Member>,
    searchQuery: String,
    subTab: String,
    onSubTabChanged: (String) -> Unit,
    onDeleteMember: (Member) -> Unit
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Sub tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (subTab == "students") MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onSubTabChanged("students") }
                    .padding(vertical = 8.dp)
                    .testTag("subtab_students"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "শিক্ষার্থী (${students.size})",
                    color = if (subTab == "students") Color.Black else MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (subTab == "teachers") MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onSubTabChanged("teachers") }
                    .padding(vertical = 8.dp)
                    .testTag("subtab_teachers"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "শিক্ষকমন্ডলী (${teachers.size})",
                    color = if (subTab == "teachers") Color.Black else MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        val activeList = if (subTab == "students") students else teachers
        val filteredList = activeList.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.classOrDesignation.contains(searchQuery, ignoreCase = true) ||
                    it.phoneNumber.contains(searchQuery)
        }

        if (filteredList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.PersonSearch, contentDescription = "", modifier = Modifier.size(72.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Text("কোনো মিল খুঁজে পাওয়া যায়নি", color = Color.Gray, fontWeight = FontWeight.Bold)
                Text("অনুগ্রহ করে অন্য নামে বা মোবাইল নাম্বারে ট্রাই করুন।", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { member ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("member_card_${member.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header: Name & Role Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = member.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = member.classOrDesignation,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (member.role == "TEACHER") Color(0xFFD97706).copy(alpha = 0.15f)
                                            else Color(0xFF3B82F6).copy(alpha = 0.15f)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (member.role == "TEACHER") "শিক্ষক" else "শিক্ষার্থী",
                                        color = if (member.role == "TEACHER") Color(0xFFD97706) else Color(0xFF3B82F6),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))

                            // Contact info layout
                            ContactInfoLine(icon = Icons.Default.Call, value = member.phoneNumber, label = "মোবাইল") {
                                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${member.phoneNumber}")))
                            }

                            if (member.email.isNotEmpty()) {
                                ContactInfoLine(icon = Icons.Default.Email, value = member.email, label = "ইমেইল") {
                                    try {
                                        val mIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${member.email}"))
                                        context.startActivity(mIntent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "মেইল অ্যাপ পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            if (member.socialAccountUrl.isNotEmpty()) {
                                ContactInfoLine(icon = Icons.Default.Share, value = member.socialAccountUrl, label = "সোশ্যাল প্রোফাইল") {
                                    var webUrl = member.socialAccountUrl
                                    if (!webUrl.startsWith("http")) webUrl = "https://$webUrl"
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)))
                                }
                            }

                            if (member.role == "STUDENT") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .background(Color.Gray.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            "পিতা/মাতা: ${member.parentName.ifEmpty { "ট্রিগার করা হয়নি" }}",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                        )
                                        if (member.parentPhoneNumber.isNotEmpty()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().clickable {
                                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${member.parentPhoneNumber}")))
                                                },
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    "অভিভাবকের নাম্বার: ${member.parentPhoneNumber}",
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Icon(Icons.Default.Call, contentDescription = "Call Parent", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = { onDeleteMember(member) },
                                    modifier = Modifier.testTag("delete_member_btn_${member.id}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete member", tint = Color.LightGray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactInfoLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, sizeModifier(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, fontSize = 9.sp, color = Color.Gray)
            Text(value, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
        }
    }
}

// 💬 Posts Feed Component
@Composable
fun PostsFeedScreenContent(
    posts: List<Post>,
    onLikePost: (Int) -> Unit,
    onDeletePost: (Post) -> Unit
) {
    if (posts.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.CardMembership, contentDescription = "", modifier = Modifier.size(64.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Text("সোশ্যাল দেয়ালে কোনো পোস্ট নেই!", color = Color.Gray)
            Text("ডানদিকের বাটনে ক্লিক করে প্রথম পোস্টটি করুন।", color = Color.Gray, fontSize = 12.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(posts) { post ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("post_card_${post.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = post.authorName.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        post.authorName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(post.timestamp)),
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (post.authorRole) {
                                            "TEACHER" -> Color(0xFFD97706).copy(alpha = 0.12f)
                                            "ADMIN" -> Color(0xFFEF4444).copy(alpha = 0.12f)
                                            else -> Color(0xFF10B981).copy(alpha = 0.12f)
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = post.authorRole,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (post.authorRole) {
                                        "TEACHER" -> Color(0xFFD97706)
                                        "ADMIN" -> Color(0xFFEF4444)
                                        else -> Color(0xFF10B981)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = post.content,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { onLikePost(post.id) }
                                    .padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ThumbUp, contentDescription = "Like", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "পছন্দ (${post.likesCount})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            IconButton(
                                onClick = { onDeletePost(post) },
                                modifier = Modifier.testTag("delete_post_btn_${post.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete post", tint = Color.LightGray.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ✉️ Inbox Simulated Chat Page
@Composable
fun InboxChatScreenContent(
    messages: List<com.example.data.Message>,
    students: List<Member>,
    teachers: List<Member>,
    onSendMessage: (senderName: String, role: String, content: String) -> Unit
) {
    var activeSenderName by remember { mutableStateOf("Tanvir Ahmed") }
    var activeSenderRole by remember { mutableStateOf("STUDENT") }
    var messageText by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    // Auto-scroll chat to latest messages on insertion
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Simulated Identity Switcher
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp)
        ) {
            Text("কার তরফ থেকে মেসেজ করতে চান? (Simulate Profile):", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    selected = activeSenderRole == "STUDENT",
                    onClick = {
                        activeSenderRole = "STUDENT"
                        activeSenderName = students.firstOrNull()?.name ?: "Tanvir Ahmed"
                    },
                    label = { Text("শিক্ষার্থী") }
                )
                FilterChip(
                    selected = activeSenderRole == "TEACHER",
                    onClick = {
                        activeSenderRole = "TEACHER"
                        activeSenderName = teachers.firstOrNull()?.name ?: "Kamal Uddin"
                    },
                    label = { Text("শিক্ষক") }
                )
                FilterChip(
                    selected = activeSenderRole == "ADMIN",
                    onClick = {
                        activeSenderRole = "ADMIN"
                        activeSenderName = "Office Admin"
                    },
                    label = { Text("এডমিন অফিস") }
                )
            }
            Text("পাঠক প্রোফাইল: $activeSenderName ($activeSenderRole)", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }

        // Messages scrolling view
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages) { msg ->
                val isOutgoing = msg.senderRole == "ADMIN" || msg.senderRole == "TEACHER"
                val alignment = if (isOutgoing) Alignment.End else Alignment.Start
                val bubbleColor = if (isOutgoing) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
                val textColor = if (isOutgoing) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = alignment
                ) {
                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isOutgoing) 16.dp else 0.dp,
                                    bottomEnd = if (isOutgoing) 0.dp else 16.dp
                                )
                            )
                            .background(bubbleColor)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        Column {
                            Text(
                                text = msg.senderName,
                                fontSize = 10.sp,
                                color = if (isOutgoing) MaterialTheme.colorScheme.primary else Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = msg.content,
                                color = textColor,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(msg.timestamp)),
                                fontSize = 8.sp,
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }

        // Bottom input field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("মেসেজ টাইপ করুন...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("inbox_chat_input"),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        onSendMessage(activeSenderName, activeSenderRole, messageText)
                        messageText = ""
                    }
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
                    .testTag("inbox_chat_send"),
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Black)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

// 📢 Notice Board Component
@Composable
fun NoticesScreenContent(
    notices: List<Notice>,
    searchQuery: String,
    onDeleteNotice: (Notice) -> Unit
) {
    val filteredNotices = notices.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.content.contains(searchQuery, ignoreCase = true)
    }

    if (filteredNotices.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Announcement, contentDescription = "", modifier = Modifier.size(64.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Text("নোটিশ বোর্ডে কোনো নোটিশ নেই", color = Color.Gray)
            Text("এডমিন প্যানেল দিয়ে নোটিশ যুক্ত করুন।", color = Color.Gray, fontSize = 12.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredNotices) { notice ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("notice_card_${notice.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (notice.category) {
                                            "EXAM" -> Color(0xFFEF4444).copy(alpha = 0.12f)
                                            "HOLIDAY" -> Color(0xFF10B981).copy(alpha = 0.12f)
                                            else -> Color(0xFF3B82F6).copy(alpha = 0.12f)
                                        }
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = when (notice.category) {
                                        "EXAM" -> "পরীক্ষা নোটিশ"
                                        "HOLIDAY" -> "ছুটির নোটিশ"
                                        else -> "সাধারণ নোটিশ"
                                    },
                                    color = when (notice.category) {
                                        "EXAM" -> Color(0xFFEF4444)
                                        "HOLIDAY" -> Color(0xFF10B981)
                                        else -> Color(0xFF3B82F6)
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date(notice.timestamp)),
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = notice.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = notice.content,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 22.sp
                        )

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = { onDeleteNotice(notice) },
                                modifier = Modifier.testTag("delete_notice_btn_${notice.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Notice", tint = Color.LightGray)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 💳 Payments Tracker Dashboard Screen
@Composable
fun PaymentsTrackerScreenContent(
    payments: List<Payment>,
    searchQuery: String,
    filterState: String, // "ALL", "PAID", "UNPAID"
    totalCollected: Double,
    totalOutstanding: Double,
    paidCount: Int,
    totalCount: Int,
    onFilterChanged: (String) -> Unit,
    onTogglePaid: (Payment) -> Unit,
    onEditClicked: (Payment) -> Unit,
    onDeletePayment: (Payment) -> Unit
) {
    // Search and filter operations
    val filteredPayments = payments.filter {
        val matchesSearch = it.studentName.contains(searchQuery, ignoreCase = true) ||
                it.className.contains(searchQuery, ignoreCase = true) ||
                it.month.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (filterState) {
            "PAID" -> it.isPaid
            "UNPAID" -> !it.isPaid
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Collections Overview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("মে ২০২৬ বকেয়া ও বেতন কালেকশন ড্যাশবোর্ড", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("মোট সংগৃহীত বেতন", color = Color.LightGray, fontSize = 11.sp)
                        Text("৳ ${totalCollected.toInt()}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("মোট বকেয়া বেতন", color = Color.LightGray, fontSize = 11.sp)
                        Text("৳ ${totalOutstanding.toInt()}", color = Color(0xFFEF4444), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("বেতন সম্পূর্ণ", color = Color.LightGray, fontSize = 11.sp)
                        Text("$paidCount / $totalCount জন", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { if (totalCount > 0) paidCount.toFloat() / totalCount.toFloat() else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.Gray.copy(alpha = 0.3f)
                )
            }
        }

        // Segmented filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = filterState == "ALL",
                onClick = { onFilterChanged("ALL") },
                label = { Text("সবাই") },
                modifier = Modifier.testTag("payment_filter_all")
            )
            FilterChip(
                selected = filterState == "PAID",
                onClick = { onFilterChanged("PAID") },
                label = { Text("পরিশোধিত") },
                modifier = Modifier.testTag("payment_filter_paid")
            )
            FilterChip(
                selected = filterState == "UNPAID",
                onClick = { onFilterChanged("UNPAID") },
                label = { Text("বকেয়া (Due)") },
                modifier = Modifier.testTag("payment_filter_unpaid")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredPayments.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CreditCardOff, contentDescription = "", modifier = Modifier.size(72.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Text("কোনো বকেয়া লিস্ট পাওয়া যায়নি", color = Color.Gray, fontWeight = FontWeight.Bold)
                Text("শিক্ষার্থী তালিকা বা ক্লাস পরিবর্তন করে খুজে দেখুন।", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPayments) { payment ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("payment_card_${payment.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = payment.studentName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row {
                                        Text(
                                            text = "${payment.className} • ",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = payment.month,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "৳ ${payment.amount.toInt()}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (payment.isPaid) Color(0xFF10B981).copy(alpha = 0.12f)
                                                else Color(0xFFEF4444).copy(alpha = 0.12f)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (payment.isPaid) "টাকা পরিশোধিত" else "টাকা বাকি (Due)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (payment.isPaid) Color(0xFF10B981) else Color(0xFFEF4444)
                                        )
                                    }
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))

                            if (payment.remarks.isNotEmpty()) {
                                Text(
                                    text = "বিশেষ নোট: ${payment.remarks}",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }

                            if (!payment.isPaid && payment.dueDate.isNotEmpty()) {
                                Text(
                                    text = "টাকা দেয়ার শেষ তারিখ: ${payment.dueDate}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )
                            } else if (payment.isPaid && payment.paymentDate > 0) {
                                Text(
                                    text = "জমা দেয়ার তারিখ: ${SimpleDateFormat("dd-MM-yyyy, hh:mm a", Locale.getDefault()).format(Date(payment.paymentDate))}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { onTogglePaid(payment) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (payment.isPaid) Color(0xFFEF4444) else Color(0xFF10B981)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("toggle_paid_btn_${payment.id}")
                                ) {
                                    Text(
                                        text = if (payment.isPaid) "বকেয়া হিসেবে চিহ্নিত করুন" else "পরিশোধিত করুন",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Row {
                                    IconButton(
                                        onClick = { onEditClicked(payment) },
                                        modifier = Modifier.testTag("edit_payment_btn_${payment.id}")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit payments", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { onDeletePayment(payment) },
                                        modifier = Modifier.testTag("delete_payment_btn_${payment.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete bill log", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun sizeModifier(dp: androidx.compose.ui.unit.Dp): Modifier {
    return Modifier.size(dp)
}
