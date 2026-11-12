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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.*
import com.example.ui.SaraswatiViewModel
import com.example.ui.theme.*

@Composable
fun CommitteeDashboardScreen(
    viewModel: SaraswatiViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val records by viewModel.academicRecords.collectAsState()
    val awards by viewModel.awards.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val pendingRecords = records.filter { it.verificationStatus == VerificationState.PENDING }
    val pendingAwards = awards.filter { it.approvalStatus == AwardApprovalState.PENDING_COMMITTEE }

    Column(modifier = modifier.fillMaxSize().background(SurfaceWhite)) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = RoyalNavy
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(if (language == AppLanguage.GUJARATI) "ગુણપત્રક ચકાસણી (${pendingRecords.size})" else "Marksheet Verification (${pendingRecords.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.VerifiedUser, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(if (language == AppLanguage.GUJARATI) "પુરસ્કાર મંજૂરી (${pendingAwards.size})" else "Award Approvals (${pendingAwards.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.FactCheck, contentDescription = null) }
            )
        }

        when (selectedTab) {
            0 -> MarksheetVerificationTab(
                language = language,
                records = records,
                onVerify = { id, approved, notes -> viewModel.verifyRecord(id, approved, notes) }
            )
            1 -> AwardApprovalTab(
                language = language,
                awards = awards,
                onDecision = { id, approved, notes -> viewModel.approveOrRejectAward(id, approved, notes) }
            )
        }
    }
}

@Composable
fun MarksheetVerificationTab(
    language: AppLanguage,
    records: List<AcademicRecordItem>,
    onVerify: (String, Boolean, String) -> Unit
) {
    var reviewingRecord by remember { mutableStateOf<AcademicRecordItem?>(null) }
    var reviewNotes by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                color = InfoBg,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = InfoBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.GUJARATI)
                            "સમિતિ નિયમ: કોઈપણ સ્વચાલિત OCR પરિણામ સીધું માન્ય ગણાશે નહીં. માનવ સમીક્ષા અને ચકાસણી ફરજિયાત છે."
                            else "Rule: OCR suggestions never auto-approve marks. Authorized human committee review is mandatory.",
                        fontSize = 12.sp,
                        color = InfoBlue
                    )
                }
            }
        }

        items(records) { rec ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("verification_record_card_${rec.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = rec.studentName, fontWeight = FontWeight.Bold, color = RoyalNavy, fontSize = 16.sp)
                            Text(text = "${rec.examinationCategory.titleEn} (${rec.academicYear}) • ${rec.school}", fontSize = 12.sp, color = TextSecondary)
                        }

                        Surface(
                            color = when (rec.verificationStatus) {
                                VerificationState.VERIFIED -> SuccessBg
                                VerificationState.PENDING -> WarningBg
                                else -> ErrorBg
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = rec.verificationStatus.name,
                                color = when (rec.verificationStatus) {
                                    VerificationState.VERIFIED -> SuccessGreen
                                    VerificationState.PENDING -> WarningOrange
                                    else -> ErrorRed
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Marks table preview
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceSubtle)
                            .padding(8.dp)
                    ) {
                        rec.subjects.forEach { subj ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = subj.subjectName, fontSize = 12.sp, color = TextPrimary)
                                Text(text = "${subj.obtainedMarks} / ${subj.maximumMarks}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = RoyalNavy)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp), color = TextMuted.copy(alpha = 0.3f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (language == AppLanguage.GUJARATI) "ટકાવારી" else "Percentage", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = "${rec.percentage}%", fontWeight = FontWeight.Bold, color = RegalGoldDark, fontSize = 13.sp)
                        }
                    }

                    if (rec.verificationStatus == VerificationState.PENDING) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onVerify(rec.id, false, "Discrepancy observed in subject marksheet")
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("reject_verification_button_${rec.id}")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == AppLanguage.GUJARATI) "નામંજૂર" else "Reject")
                            }

                            Button(
                                onClick = {
                                    onVerify(rec.id, true, "Verified against physical board marksheet copy")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("approve_verification_button_${rec.id}")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == AppLanguage.GUJARATI) "પ્રમાણિત કરો" else "Verify & Approve")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AwardApprovalTab(
    language: AppLanguage,
    awards: List<AwardGrant>,
    onDecision: (String, Boolean, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(awards) { award ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("committee_award_card_${award.id}")
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
                                fontWeight = FontWeight.Bold,
                                color = RoyalNavy,
                                fontSize = 15.sp
                            )
                            Text(text = "Student: ${award.studentName} • ${award.academicYear}", fontSize = 12.sp, color = TextSecondary)
                        }

                        Surface(color = GoldContainer, shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = "₹${"%,d".format(award.amount)}",
                                color = OnGoldContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Evidence: ${award.eligibilityEvidence}", fontSize = 12.sp, color = TextPrimary)

                    Spacer(modifier = Modifier.height(10.dp))

                    if (award.approvalStatus == AwardApprovalState.PENDING_COMMITTEE) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onDecision(award.id, false, "Overlapping category or quota rule limit") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                modifier = Modifier.weight(1f).testTag("reject_award_button_${award.id}")
                            ) {
                                Text(if (language == AppLanguage.GUJARATI) "નામંજૂર" else "Decline")
                            }

                            Button(
                                onClick = { onDecision(award.id, true, "Confirmed by Education Award Committee") },
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                                modifier = Modifier.weight(1f).testTag("approve_award_button_${award.id}")
                            ) {
                                Text(if (language == AppLanguage.GUJARATI) "મંજૂર કરો" else "Approve Award")
                            }
                        }
                    } else {
                        Surface(
                            color = if (award.approvalStatus == AwardApprovalState.APPROVED) SuccessBg else ErrorBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Committee Status: ${award.approvalStatus.name}",
                                color = if (award.approvalStatus == AwardApprovalState.APPROVED) SuccessGreen else ErrorRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
