package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.*
import com.example.ui.SaraswatiViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    viewModel: SaraswatiViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val students by viewModel.students.collectAsState()
    val awards by viewModel.awards.collectAsState()
    val records by viewModel.academicRecords.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showRegistrationDialog by remember { mutableStateOf(false) }
    var showMarksheetDialog by remember { mutableStateOf(false) }

    // Use current or first student
    val currentStudent = students.firstOrNull()
    val studentAwards = awards.filter { it.studentId == (currentStudent?.id ?: "") }
    val approvedTotal = currentStudent?.let { viewModel.getConsolidatedApprovedTotal(it.id) } ?: 0

    Column(modifier = modifier.fillMaxSize().background(SurfaceWhite)) {
        // Tab Bar
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = RoyalNavy
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(if (language == AppLanguage.GUJARATI) "મારા પુરસ્કારો" else "My Stacked Awards", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = RegalGoldDark) },
                modifier = Modifier.testTag("tab_my_awards")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(if (language == AppLanguage.GUJARATI) "ગુણપત્રક અરજી" else "Academic Record", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.School, contentDescription = null) },
                modifier = Modifier.testTag("tab_academic_record")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text(if (language == AppLanguage.GUJARATI) "પ્રોફાઇલ અને વાલી" else "Student Profile", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.testTag("tab_profile")
            )
        }

        when (selectedTab) {
            0 -> MyAwardsTab(
                language = language,
                student = currentStudent,
                awards = studentAwards,
                approvedTotal = approvedTotal,
                onApplyClick = { showMarksheetDialog = true }
            )
            1 -> AcademicRecordTab(
                language = language,
                records = records.filter { it.studentId == (currentStudent?.id ?: "") },
                onAddRecordClick = { showMarksheetDialog = true }
            )
            2 -> StudentProfileTab(
                language = language,
                student = currentStudent,
                onEditClick = { showRegistrationDialog = true }
            )
        }
    }

    if (showRegistrationDialog) {
        StudentRegistrationDialog(
            language = language,
            onDismiss = { showRegistrationDialog = false },
            onSubmit = { name, nameGu, guardian, mobile, school, std ->
                viewModel.registerStudent(name, nameGu, guardian, mobile, school, std)
                showRegistrationDialog = false
            }
        )
    }

    if (showMarksheetDialog) {
        SubmitMarksheetDialog(
            language = language,
            student = currentStudent,
            onDismiss = { showMarksheetDialog = false },
            onSubmit = { year, stream, school, subjects ->
                if (currentStudent != null) {
                    viewModel.submitAcademicRecord(
                        studentId = currentStudent.id,
                        studentName = currentStudent.fullName,
                        academicYear = year,
                        examStream = stream,
                        school = school,
                        subjects = subjects
                    )
                }
                showMarksheetDialog = false
            }
        )
    }
}

@Composable
fun MyAwardsTab(
    language: AppLanguage,
    student: StudentProfile?,
    awards: List<AwardGrant>,
    approvedTotal: Int,
    onApplyClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Multi-Award Stacking Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = RoyalNavy),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("consolidated_award_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.GUJARATI) "કુલ મંજૂર પુરસ્કાર ગ્રાન્ટ" else "Consolidated Approved Award Grant",
                                color = RegalGoldLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹${"%,d".format(approvedTotal)}",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(RegalGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = RoyalNavy,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val sName = if (language == AppLanguage.GUJARATI && !student?.fullNameGujarati.isNullOrEmpty()) {
                            student?.fullNameGujarati ?: "N/A"
                        } else {
                            student?.fullName ?: "N/A"
                        }
                        Text(
                            text = if (language == AppLanguage.GUJARATI) "વિદ્યાર્થી: $sName" else "Student: $sName",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                        Surface(
                            color = RegalGold,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.GUJARATI) 
                                    "${awards.count { it.approvalStatus == AwardApprovalState.APPROVED }} પુરસ્કાર મંજૂર" 
                                    else "${awards.count { it.approvalStatus == AwardApprovalState.APPROVED }} Awards Stacked",
                                color = RoyalNavy,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Multi-Award Rule Callout
        item {
            Surface(
                color = GoldContainer,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, RegalGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = RegalGoldDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (language == AppLanguage.GUJARATI)
                            "વિદ્યાર્થી એક જ વર્ષમાં બોર્ડ પ્રથમ રેન્ક, વિષયવાર સર્વોચ્ચ ગુણ (અંગ્રેજી, વિજ્ઞાન), શ્રેષ્ઠ પરિણામ અને દાતાશ્રી વિશેષ એમ બહુવિધ પુરસ્કારો મેળવવા પાત્ર છે."
                            else "A student can receive MULTIPLE different awards in the same academic year (Overall Rank, Subject Toppers, Passing & Memorial Awards).",
                        fontSize = 12.sp,
                        color = OnGoldContainer,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Stacked Awards List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.GUJARATI) "વ્યક્તિગત પુરસ્કારોની યાદી" else "Individual Stacked Awards",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
                Text(
                    text = "${awards.size} ${if (language == AppLanguage.GUJARATI) "પુરસ્કારો" else "Awards"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
        }

        if (awards.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (language == AppLanguage.GUJARATI) "હજુ સુધી કોઈ પુરસ્કાર ફાળવાયા નથી" else "No awards granted yet",
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onApplyClick,
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (language == AppLanguage.GUJARATI) "માર્કશીટ અપલોડ કરો" else "Submit Marksheet")
                        }
                    }
                }
            }
        } else {
            items(awards) { award ->
                AwardGrantCard(award = award, language = language)
            }
        }
    }
}

@Composable
fun AwardGrantCard(award: AwardGrant, language: AppLanguage) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("award_grant_card_${award.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == AppLanguage.GUJARATI) award.awardTitleGujarati else award.awardTitle,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = RoyalNavy)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${award.academicYear} • ${award.examinationCategory.titleEn}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Surface(
                    color = GoldContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "₹${"%,d".format(award.amount)}",
                        color = OnGoldContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Eligibility Evidence
            Surface(
                color = SurfaceSubtle,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = award.eligibilityEvidence,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Status badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Approval Status
                    val (appBg, appFg, appLabel) = when (award.approvalStatus) {
                        AwardApprovalState.APPROVED -> Triple(SuccessBg, SuccessGreen, if (language == AppLanguage.GUJARATI) "મંજૂર" else "Approved")
                        AwardApprovalState.PENDING_COMMITTEE -> Triple(WarningBg, WarningOrange, if (language == AppLanguage.GUJARATI) "સમિતિ પેન્ડિંગ" else "Pending Committee")
                        AwardApprovalState.REJECTED -> Triple(ErrorBg, ErrorRed, if (language == AppLanguage.GUJARATI) "નામંજૂર" else "Rejected")
                        else -> Triple(SurfaceSubtle, TextSecondary, award.approvalStatus.name)
                    }
                    Surface(color = appBg, shape = RoundedCornerShape(4.dp)) {
                        Text(text = appLabel, color = appFg, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }

                    // Payment Status
                    val (payBg, payFg, payLabel) = when (award.paymentStatus) {
                        PaymentState.PAID -> Triple(SuccessBg, SuccessGreen, if (language == AppLanguage.GUJARATI) "ચુકવણી પૂર્ણ" else "Paid")
                        PaymentState.APPROVED -> Triple(InfoBg, InfoBlue, if (language == AppLanguage.GUJARATI) "ચુકવણી મંજૂર" else "Payment Approved")
                        PaymentState.PENDING -> Triple(SurfaceSubtle, TextSecondary, if (language == AppLanguage.GUJARATI) "ચુકવણી બાકી" else "Payment Pending")
                        else -> Triple(ErrorBg, ErrorRed, award.paymentStatus.name)
                    }
                    Surface(color = payBg, shape = RoundedCornerShape(4.dp)) {
                        Text(text = payLabel, color = payFg, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                if (award.receiptNumber != null) {
                    Text(
                        text = "Rcpt: ${award.receiptNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun AcademicRecordTab(
    language: AppLanguage,
    records: List<AcademicRecordItem>,
    onAddRecordClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.GUJARATI) "શૈક્ષણિક પરિણામો અને ગુણપત્રક" else "Academic Records & Marksheets",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RoyalNavy)
                )
                Button(
                    onClick = onAddRecordClick,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_marksheet_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (language == AppLanguage.GUJARATI) "નવું ઉમેરો" else "Submit Marks")
                }
            }
        }

        // Gujarati OCR Advisory Card
        item {
            Surface(
                color = InfoBg,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, InfoBlue.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = InfoBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.get("ocr_gujarati_notice", language),
                        fontSize = 12.sp,
                        color = InfoBlue,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        if (records.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (language == AppLanguage.GUJARATI) "કોઈ શૈક્ષણિક રેકોર્ડ જમા થયેલ નથી" else "No academic records submitted yet",
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(records) { record ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${record.examinationCategory.titleEn} (${record.academicYear})",
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalNavy,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${record.board} • ${record.school}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                            Surface(
                                color = if (record.verificationStatus == VerificationState.VERIFIED) SuccessBg else WarningBg,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (record.verificationStatus == VerificationState.VERIFIED) 
                                        (if (language == AppLanguage.GUJARATI) "પ્રમાણિત" else "VERIFIED")
                                        else (if (language == AppLanguage.GUJARATI) "સમીક્ષા હેઠળ" else "UNDER REVIEW"),
                                    color = if (record.verificationStatus == VerificationState.VERIFIED) SuccessGreen else WarningOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Subject Marks Breakdown Table
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceSubtle)
                                .padding(8.dp)
                        ) {
                            record.subjects.forEach { subj ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = subj.subjectName, fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                                    Text(
                                        text = "${subj.obtainedMarks} / ${subj.maximumMarks}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = RoyalNavy
                                    )
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 6.dp), color = TextMuted.copy(alpha = 0.3f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = if (language == AppLanguage.GUJARATI) "કુલ ટકાવારી" else "Total Percentage", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = "${record.percentage}% (${record.totalObtainedMarks}/${record.totalMaximumMarks})",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RegalGoldDark,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        if (record.verificationRemarks.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Committee Note: ${record.verificationRemarks}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentProfileTab(
    language: AppLanguage,
    student: StudentProfile?,
    onEditClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.GUJARATI) "વિદ્યાર્થી પ્રોફાઇલ" else "Student Identity",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RoyalNavy)
                        )
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = RoyalNavy)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    ProfileItemRow(
                        label = if (language == AppLanguage.GUJARATI) "પૂરું નામ" else "Full Name",
                        value = student?.fullName ?: "N/A"
                    )
                    ProfileItemRow(
                        label = if (language == AppLanguage.GUJARATI) "ગુજરાતીમાં નામ" else "Name (Gujarati)",
                        value = student?.fullNameGujarati?.ifEmpty { "આરવ પ્રવીણ પટેલ" } ?: "N/A"
                    )
                    ProfileItemRow(
                        label = if (language == AppLanguage.GUJARATI) "રજીસ્ટ્રેશન કોડ" else "Registration Code",
                        value = student?.registrationCode ?: "N/A"
                    )
                    ProfileItemRow(
                        label = if (language == AppLanguage.GUJARATI) "શાળાનું નામ" else "School Name",
                        value = student?.schoolName ?: "N/A"
                    )
                    ProfileItemRow(
                        label = if (language == AppLanguage.GUJARATI) "ધોરણ" else "Standard",
                        value = student?.standard ?: "N/A"
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = AppStrings.get("guardian_consent", language),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = RoyalNavy)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ProfileItemRow(
                        label = if (language == AppLanguage.GUJARATI) "વાલીનું નામ" else "Guardian Name",
                        value = student?.guardianName ?: "N/A"
                    )
                    ProfileItemRow(
                        label = if (language == AppLanguage.GUJARATI) "વાલીનો મોબાઈલ" else "Guardian Mobile",
                        value = student?.guardianMobile ?: "N/A"
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = SuccessBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.get("guardian_consent_desc", language),
                                fontSize = 11.sp,
                                color = SuccessGreen,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileItemRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = TextPrimary))
    }
}

@Composable
fun StudentRegistrationDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, String, String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var fullNameGu by remember { mutableStateOf("") }
    var guardianName by remember { mutableStateOf("") }
    var guardianMobile by remember { mutableStateOf("") }
    var schoolName by remember { mutableStateOf("") }
    var standard by remember { mutableStateOf("Std 10 (SSC)") }
    var consentChecked by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (language == AppLanguage.GUJARATI) "વિદ્યાર્થી નોંધણી" else "Student Registration",
                fontWeight = FontWeight.Bold,
                color = RoyalNavy
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(if (language == AppLanguage.GUJARATI) "વિદ્યાર્થીનું પૂરું નામ" else "Student Full Name") },
                    modifier = Modifier.fillMaxWidth().testTag("reg_full_name")
                )
                OutlinedTextField(
                    value = fullNameGu,
                    onValueChange = { fullNameGu = it },
                    label = { Text(if (language == AppLanguage.GUJARATI) "ગુજરાતીમાં નામ" else "Name in Gujarati") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = guardianName,
                    onValueChange = { guardianName = it },
                    label = { Text(if (language == AppLanguage.GUJARATI) "વાલીનું નામ" else "Guardian Name") },
                    modifier = Modifier.fillMaxWidth().testTag("reg_guardian_name")
                )
                OutlinedTextField(
                    value = guardianMobile,
                    onValueChange = { guardianMobile = it },
                    label = { Text(if (language == AppLanguage.GUJARATI) "વાલીનો મોબાઈલ" else "Guardian Mobile") },
                    modifier = Modifier.fillMaxWidth().testTag("reg_guardian_mobile")
                )
                OutlinedTextField(
                    value = schoolName,
                    onValueChange = { schoolName = it },
                    label = { Text(if (language == AppLanguage.GUJARATI) "શાળાનું નામ" else "School Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = consentChecked, onCheckedChange = { consentChecked = it })
                    Text(
                        text = if (language == AppLanguage.GUJARATI) "વાલીની સંમતિ માન્ય છે" else "Required Guardian Consent Confirmed",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (consentChecked && fullName.isNotBlank()) {
                        onSubmit(fullName, fullNameGu, guardianName, guardianMobile, schoolName, standard)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                modifier = Modifier.testTag("submit_registration_confirm_button")
            ) {
                Text(if (language == AppLanguage.GUJARATI) "નોંધણી કરો" else "Register")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == AppLanguage.GUJARATI) "રદ કરો" else "Cancel")
            }
        }
    )
}

@Composable
fun SubmitMarksheetDialog(
    language: AppLanguage,
    student: StudentProfile?,
    onDismiss: () -> Unit,
    onSubmit: (String, ExamStream, String, List<SubjectScore>) -> Unit
) {
    var academicYear by remember { mutableStateOf("2024-2025") }
    var selectedStream by remember { mutableStateOf(ExamStream.SSC) }
    var schoolName by remember { mutableStateOf(student?.schoolName ?: "") }

    var mathMarks by remember { mutableStateOf("95") }
    var scienceMarks by remember { mutableStateOf("96") }
    var englishMarks by remember { mutableStateOf("94") }
    var gujaratiMarks by remember { mutableStateOf("92") }
    var ssMarks by remember { mutableStateOf("93") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (language == AppLanguage.GUJARATI) "ગુણપત્રક અને માર્ક્સ સબમિટ કરો" else "Submit Marksheet & Marks",
                fontWeight = FontWeight.Bold,
                color = RoyalNavy
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sTitleName = student?.fullName ?: "N/A"
                Text(
                    text = if (language == AppLanguage.GUJARATI) "વિદ્યાર્થી: $sTitleName" else "Student: $sTitleName",
                    fontWeight = FontWeight.SemiBold,
                    color = RegalGoldDark
                )

                // Gujarati OCR Advisory
                Surface(color = InfoBg, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = if (language == AppLanguage.GUJARATI)
                            "નોંધ: ગુજરાતી માર્કશીટ માટે મેન્યુઅલ એન્ટ્રી અને સમિતિ વેરિફિકેશન લાગુ રહેશે."
                            else "Notice: Gujarati marksheets require manual entry and committee review.",
                        fontSize = 11.sp,
                        color = InfoBlue,
                        modifier = Modifier.padding(6.dp)
                    )
                }

                OutlinedTextField(
                    value = schoolName,
                    onValueChange = { schoolName = it },
                    label = { Text(if (language == AppLanguage.GUJARATI) "શાળા" else "School") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = if (language == AppLanguage.GUJARATI) "વિષયવાર ગુણ (૧૦૦ માંથી):" else "Subject-wise Marks (Out of 100):",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = TextPrimary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = englishMarks,
                        onValueChange = { englishMarks = it },
                        label = { Text("English") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = scienceMarks,
                        onValueChange = { scienceMarks = it },
                        label = { Text("Science") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = mathMarks,
                        onValueChange = { mathMarks = it },
                        label = { Text("Maths") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = gujaratiMarks,
                        onValueChange = { gujaratiMarks = it },
                        label = { Text("Gujarati") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = ssMarks,
                    onValueChange = { ssMarks = it },
                    label = { Text("Social Science") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val subjects = listOf(
                        SubjectScore("English Second Language", 100, englishMarks.toIntOrNull() ?: 0),
                        SubjectScore("Science & Technology", 100, scienceMarks.toIntOrNull() ?: 0),
                        SubjectScore("Mathematics", 100, mathMarks.toIntOrNull() ?: 0),
                        SubjectScore("Gujarati First Language", 100, gujaratiMarks.toIntOrNull() ?: 0),
                        SubjectScore("Social Science", 100, ssMarks.toIntOrNull() ?: 0)
                    )
                    onSubmit(academicYear, selectedStream, schoolName, subjects)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                modifier = Modifier.testTag("submit_marksheet_confirm_button")
            ) {
                Text(if (language == AppLanguage.GUJARATI) "સબમિટ કરો" else "Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == AppLanguage.GUJARATI) "રદ કરો" else "Cancel")
            }
        }
    )
}
