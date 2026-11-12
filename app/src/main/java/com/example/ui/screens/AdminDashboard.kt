package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import com.example.domain.*
import com.example.data.sync.SheetSyncResult
import com.example.ui.SaraswatiViewModel
import com.example.ui.ads.AdMobManager
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    viewModel: SaraswatiViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val activeSociety by viewModel.activeSociety.collectAsState()
    val students by viewModel.students.collectAsState()
    val records by viewModel.academicRecords.collectAsState()
    val rules by viewModel.awardRules.collectAsState()
    val awards by viewModel.awards.collectAsState()
    val memorialFunds by viewModel.memorialFunds.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    var selectedAdminTab by remember { mutableStateOf(0) }

    Column(modifier = modifier.fillMaxSize().background(SurfaceWhite)) {
        // Admin Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedAdminTab,
            containerColor = Color.White,
            contentColor = RoyalNavy,
            edgePadding = 12.dp
        ) {
            Tab(
                selected = selectedAdminTab == 0,
                onClick = { selectedAdminTab = 0 },
                text = { Text(if (language == AppLanguage.GUJARATI) "ડેશબોર્ડ અને એન્જિન" else "Overview & Engine", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = null) }
            )
            Tab(
                selected = selectedAdminTab == 1,
                onClick = { selectedAdminTab = 1 },
                text = { Text(if (language == AppLanguage.GUJARATI) "વિદ્યાર્થીઓ (${students.size})" else "Students (${students.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.People, contentDescription = null) }
            )
            Tab(
                selected = selectedAdminTab == 2,
                onClick = { selectedAdminTab = 2 },
                text = { Text(if (language == AppLanguage.GUJARATI) "પુરસ્કાર નિયમો (${rules.size})" else "Award Rules (${rules.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Rule, contentDescription = null) }
            )
            Tab(
                selected = selectedAdminTab == 3,
                onClick = { selectedAdminTab = 3 },
                text = { Text(if (language == AppLanguage.GUJARATI) "ઓડિટ લોગ" else "Audit Trail", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.History, contentDescription = null) }
            )
            Tab(
                selected = selectedAdminTab == 4,
                onClick = { selectedAdminTab = 4 },
                text = { Text(if (language == AppLanguage.GUJARATI) "ગૂગલ શીટ ક્લાઉડ DB" else "Google Sheets DB", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.TableChart, contentDescription = null, tint = SuccessGreen) }
            )
        }

        val context = LocalContext.current
        val activity = context as? Activity

        when (selectedAdminTab) {
            0 -> AdminOverviewTab(
                language = language,
                society = activeSociety,
                studentsCount = students.size,
                pendingRecordsCount = records.count { it.verificationStatus == VerificationState.PENDING },
                totalAwards = awards.size,
                totalDisbursed = awards.filter { it.paymentStatus == PaymentState.PAID }.sumOf { it.amount },
                onRunEngine = {
                    AdMobManager.showInterstitial(activity) {
                        viewModel.runAwardEngine("2024-2025")
                    }
                }
            )
            1 -> AdminStudentsTab(language = language, students = students, records = records)
            2 -> AdminAwardRulesTab(language = language, rules = rules)
            3 -> AdminAuditLogTab(language = language, logs = auditLogs)
            4 -> AdminGoogleSheetsTab(language = language, viewModel = viewModel)
        }
    }
}

@Composable
fun AdminOverviewTab(
    language: AppLanguage,
    society: SocietyItem?,
    studentsCount: Int,
    pendingRecordsCount: Int,
    totalAwards: Int,
    totalDisbursed: Int,
    onRunEngine: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Society Title Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = RoyalNavy),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.GUJARATI) 
                            society?.nameGujarati ?: society?.name ?: "સમાજ પ્રશાસન" 
                            else society?.name ?: "Society Administration",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val reg = society?.registrationNumber ?: "N/A"
                    val code = society?.code ?: "N/A"
                    Text(
                        text = "Registration: $reg • Code: $code",
                        color = RegalGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = if (language == AppLanguage.GUJARATI) "વિદ્યાર્થીઓ" else "Total Students",
                        value = "$studentsCount",
                        icon = Icons.Default.School,
                        color = RoyalNavy
                    )
                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = if (language == AppLanguage.GUJARATI) "માર્કશીટ સમીક્ષા" else "Pending Review",
                        value = "$pendingRecordsCount",
                        icon = Icons.Default.PendingActions,
                        color = WarningOrange
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = if (language == AppLanguage.GUJARATI) "ફાળવેલ પુરસ્કારો" else "Awards Granted",
                        value = "$totalAwards",
                        icon = Icons.Default.EmojiEvents,
                        color = RegalGoldDark
                    )
                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = if (language == AppLanguage.GUJARATI) "કુલ ચુકવાયેલ" else "Total Disbursed",
                        value = "₹${"%,d".format(totalDisbursed)}",
                        icon = Icons.Default.Payments,
                        color = SuccessGreen
                    )
                }
            }
        }

        // Multiple Award Engine CTA Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth().testTag("award_engine_cta_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RegalGoldDark)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.GUJARATI) "મલ્ટીપલ એવોર્ડ એન્જિન (2024-2025)" else "Multiple Award Engine (2024-2025)",
                                fontWeight = FontWeight.Bold,
                                color = RoyalNavy,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (language == AppLanguage.GUJARATI) "પ્રમાણિત માર્કશીટ પરથી પુરસ્કારોની આપમેળે ગણતરી" else "Stack multiple awards across distinct categories",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (language == AppLanguage.GUJARATI)
                            "• એક વિદ્યાર્થીને રેન્ક, વિષય સર્વોચ્ચ ગુણ, ઉત્સાહ પુરસ્કાર અને દાતાશ્રી પુરસ્કાર અલગ-અલગ ફાળવવામાં આવશે.\n• સમાન નિયમ હેઠળ ડુપ્લિકેટ અટકાવવામાં આવશે.\n• સમિતિ મંજૂરી મેળવ્યા પછી જ ચુકવણી થશે."
                            else "• Evaluates Top Rank, Subject Toppers (English, Science), Passing and Donor Special awards.\n• Enforces multi-award stacking while preventing duplicates under same rule.\n• Sends awards to Committee for final sign-off.",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onRunEngine,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("run_award_engine_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.GUJARATI) "પુરસ્કાર ગણતરી શરૂ કરો (Run Engine)" else "Execute Award Engine Cycle",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 12.sp, color = TextSecondary)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun AdminStudentsTab(
    language: AppLanguage,
    students: List<StudentProfile>,
    records: List<AcademicRecordItem>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(students) { student ->
            val record = records.find { it.studentId == student.id }
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == AppLanguage.GUJARATI && student.fullNameGujarati.isNotEmpty()) student.fullNameGujarati else student.fullName,
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavy,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Code: ${student.registrationCode} • Guardian: ${student.guardianName}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "School: ${student.schoolName}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    if (record != null) {
                        Surface(
                            color = if (record.verificationStatus == VerificationState.VERIFIED) SuccessBg else WarningBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${record.percentage}%",
                                color = if (record.verificationStatus == VerificationState.VERIFIED) SuccessGreen else WarningOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAwardRulesTab(
    language: AppLanguage,
    rules: List<AwardRuleItem>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Surface(
                color = GoldContainer,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (language == AppLanguage.GUJARATI)
                        "નિયમો: એક વિદ્યાર્થી માટે વિવિધ કેટેગરીના અલગ-અલગ પુરસ્કારો સ્ટેક (સંચિત) થઈ શકે છે."
                        else "Rules Engine: Distinct rules can be stacked for the same student in the same year.",
                    color = OnGoldContainer,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        items(rules) { rule ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.GUJARATI) rule.titleGujarati else rule.title,
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavy,
                            fontSize = 14.sp
                        )
                        Surface(color = GoldContainer, shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = "₹${"%,d".format(rule.cashAmount)}",
                                color = OnGoldContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Exam: ${rule.examinationCategory.titleEn} • Academic Year: ${rule.academicYear}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    val criteria = when {
                        rule.rankTarget != null -> "Target: Rank ${rule.rankTarget} (Min ${rule.minimumPercentageThreshold ?: 0}%)"
                        rule.subjectTarget != null -> "Subject Topper: ${rule.subjectTarget}"
                        else -> "Merit Threshold: ${rule.minimumPercentageThreshold}%"
                    }
                    Text(text = "Criteria: $criteria • Tie: ${rule.tiePolicy}", fontSize = 11.sp, color = TextMuted)
                }
            }
        }
    }
}

@Composable
fun AdminAuditLogTab(
    language: AppLanguage,
    logs: List<AuditLogRecord>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(logs) { log ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = log.action, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AccentBlue)
                        Text(text = log.timestamp, fontSize = 11.sp, color = TextMuted)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = log.details, fontSize = 12.sp, color = TextPrimary)
                    Text(text = "By: ${log.performedBy}", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun AdminGoogleSheetsTab(
    language: AppLanguage,
    viewModel: SaraswatiViewModel
) {
    val activeSociety by viewModel.activeSociety.collectAsState()
    var spreadsheetId by remember(activeSociety?.id) { 
        mutableStateOf(activeSociety?.spreadsheetId ?: "") 
    }
    val syncResult by viewModel.sheetSyncResult.collectAsState()
    var selectedTabPreview by remember { mutableStateOf<String?>(null) }
    var tabPreviewCsv by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Integration Status Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = RoyalNavy),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("google_sheets_banner_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.GUJARATI) "સમાજવાર ગૂગલ શીટ્સ ક્લાઉડ ડેટાબેઝ" else "Samaj-Isolated Google Sheets Cloud DB",
                                color = RegalGoldLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = activeSociety?.name ?: "Samaj Database",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            color = SuccessBg,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Dedicated Sheet",
                                    color = SuccessGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (language == AppLanguage.GUJARATI)
                            "• દરેક સમાજ પાસે પોતાનું સમર્પિત ગૂગલ શીટ છે.\n• અન્ય કોઈ સમાજ આ સમાજનો ડેટા જોઈ કે સુધારી શકશે નહીં.\n• લોકલ SQLite માં પણ સમાજવાર સંપૂર્ણ સુરક્ષિત અલગતા છે."
                            else "• Each Samaj maintains its own independent Google Spreadsheet ID.\n• Strict Tenant Isolation: Other societies CANNOT view or access this Samaj's data.\n• Two-way sync keeps local SQLite and this Samaj's Google Sheet in lockstep.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Spreadsheet Configuration & Sync CTA
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val socName = activeSociety?.name ?: "This Samaj"
                    Text(
                        text = if (language == AppLanguage.GUJARATI) 
                            "$socName નું ગૂગલ સ્પ્રેડશીટ ID" 
                            else "Google Spreadsheet ID for: $socName",
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = spreadsheetId,
                        onValueChange = { spreadsheetId = it },
                        label = { Text("Google Spreadsheet ID / Key") },
                        modifier = Modifier.fillMaxWidth().testTag("spreadsheet_id_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                activeSociety?.id?.let { socId ->
                                    viewModel.updateSocietySpreadsheetId(socId, spreadsheetId)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("save_spreadsheet_id_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.GUJARATI) "ID સાચવો (Save)" else "Save ID",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        val context = LocalContext.current
                        val activity = context as? Activity

                        Button(
                            onClick = {
                                activeSociety?.id?.let { socId ->
                                    viewModel.updateSocietySpreadsheetId(socId, spreadsheetId)
                                }
                                AdMobManager.showInterstitial(activity) {
                                    viewModel.syncWithGoogleSheets(spreadsheetId)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.3f).testTag("sync_google_sheets_now_button")
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.GUJARATI) "સિંક કરો (Sync)" else "Sync Sheet",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    val currentSync = syncResult
                    if (currentSync != null) {
                        Surface(
                            color = SuccessBg,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = currentSync.message,
                                    fontSize = 12.sp,
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Last synced: ${currentSync.timestamp} • Tabs: ${currentSync.syncedTabs.joinToString()}",
                                    fontSize = 11.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Google Sheets Database Tabs Quick Viewer
        item {
            Text(
                text = if (language == AppLanguage.GUJARATI) "ગૂગલ શીટ ટેબ્સ ડેટા (Google Sheet Tabs)" else "Google Sheet Database Tables (Tabs)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RoyalNavy)
            )
        }

        val tabs = listOf("Societies", "Students", "AcademicRecords", "Awards", "MemorialFunds", "Payments")
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tabs.take(3).forEach { tab ->
                    FilterChip(
                        selected = selectedTabPreview == tab,
                        onClick = {
                            selectedTabPreview = tab
                            tabPreviewCsv = viewModel.getGoogleSheetCsvForTab(tab)
                        },
                        label = { Text(tab, fontSize = 11.sp) }
                    )
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tabs.drop(3).forEach { tab ->
                    FilterChip(
                        selected = selectedTabPreview == tab,
                        onClick = {
                            selectedTabPreview = tab
                            tabPreviewCsv = viewModel.getGoogleSheetCsvForTab(tab)
                        },
                        label = { Text(tab, fontSize = 11.sp) }
                    )
                }
            }
        }

        if (tabPreviewCsv != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sheet Tab: [${selectedTabPreview}] Content",
                                color = RegalGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { tabPreviewCsv = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                        Text(
                            text = tabPreviewCsv!!,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
