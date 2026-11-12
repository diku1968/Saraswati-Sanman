package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.*
import com.example.ui.SaraswatiViewModel
import com.example.ui.theme.*

@Composable
fun ReportsScreen(
    viewModel: SaraswatiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.currentLanguage.collectAsState()
    val awards by viewModel.awards.collectAsState()
    val funds by viewModel.memorialFunds.collectAsState()
    val payments by viewModel.payments.collectAsState()

    var selectedReportType by remember { mutableStateOf(0) }
    var previewCsvText by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxSize().background(SurfaceWhite)) {
        ScrollableTabRow(
            selectedTabIndex = selectedReportType,
            containerColor = Color.White,
            contentColor = RoyalNavy,
            edgePadding = 12.dp
        ) {
            Tab(
                selected = selectedReportType == 0,
                onClick = { selectedReportType = 0; previewCsvText = null },
                text = { Text(if (language == AppLanguage.GUJARATI) "વાર્ષિક પુરસ્કાર રજિસ્ટર" else "Annual Award Register", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedReportType == 1,
                onClick = { selectedReportType = 1; previewCsvText = null },
                text = { Text(if (language == AppLanguage.GUJARATI) "FD અને સ્મૃતિ ફંડ અહેવાલ" else "FD & Memorial Funds", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedReportType == 2,
                onClick = { selectedReportType = 2; previewCsvText = null },
                text = { Text(if (language == AppLanguage.GUJARATI) "ચુકવણી રજિસ્ટર" else "Payment Register", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedReportType == 3,
                onClick = { selectedReportType = 3; previewCsvText = null },
                text = { Text(if (language == AppLanguage.GUJARATI) "વાર્ષિક નાણાકીય સારાંશ" else "Financial Summary", fontWeight = FontWeight.Bold) }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Export Action Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.GUJARATI) "દ્વિભાષી અહેવાલ નિકાસ (Bilingual Export)" else "Bilingual Report Generator",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = RoyalNavy)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                viewModel.syncWithGoogleSheets()
                                Toast.makeText(context, "Synced to Google Sheets Database!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("reports_sync_google_sheets_button")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp), tint = SuccessGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sheet Sync", color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val csv = when (selectedReportType) {
                                    0 -> viewModel.exportAwardRegisterCsv()
                                    1 -> viewModel.exportFdInterestCsv()
                                    else -> viewModel.exportAwardRegisterCsv()
                                }
                                previewCsvText = csv
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Saraswati_Samman_Report", csv)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "CSV copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("export_csv_button")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (language == AppLanguage.GUJARATI) "CSV" else "CSV Export")
                        }
                    }
                }
            }

            // CSV Preview Box if generated
            if (previewCsvText != null) {
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
                                    text = "Generated CSV / Excel Output (${language.displayName})",
                                    color = RegalGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = { previewCsvText = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = previewCsvText!!,
                                color = Color.White.copy(alpha = 0.9f),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Render Report View
            when (selectedReportType) {
                0 -> {
                    item {
                        Text(
                            text = if (language == AppLanguage.GUJARATI) "વાર્ષિક પુરસ્કારોની યાદી (2024-2025)" else "Annual Awards List (2024-2025)",
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                    items(awards) { aw ->
                        ReportAwardRow(award = aw, language = language)
                    }
                }
                1 -> {
                    item {
                        Text(
                            text = if (language == AppLanguage.GUJARATI) "FD મુદ્દલ અને પ્રાપ્ત વ્યાજ અહેવાલ" else "FD Principal & Actual Interest Report",
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                    items(funds) { fund ->
                        ReportFundRow(fund = fund, language = language)
                    }
                }
                2 -> {
                    item {
                        Text(
                            text = if (language == AppLanguage.GUJARATI) "ચુકવણી અને રસીદ રજિસ્ટર" else "Disbursement Payment Register",
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                    items(payments) { p ->
                        ReportPaymentRow(payment = p)
                    }
                }
                3 -> {
                    item {
                        AnnualFinancialSummaryCard(
                            language = language,
                            awards = awards,
                            funds = funds,
                            payments = payments
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportAwardRow(award: AwardGrant, language: AppLanguage) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (language == AppLanguage.GUJARATI) award.awardTitleGujarati else award.awardTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = RoyalNavy
                )
                Text(text = "Student: ${award.studentName} • ${award.academicYear}", fontSize = 11.sp, color = TextSecondary)
                Text(text = "Status: ${award.approvalStatus} | Payment: ${award.paymentStatus}", fontSize = 10.sp, color = TextMuted)
            }
            Text(
                text = "₹${"%,d".format(award.amount)}",
                fontWeight = FontWeight.Bold,
                color = RegalGoldDark,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ReportFundRow(fund: MemorialFundItem, language: AppLanguage) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = if (language == AppLanguage.GUJARATI) fund.fundNameGujarati else fund.fundName,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = RoyalNavy
            )
            Text(text = "Dedicated: ${fund.inMemoryOf}", fontSize = 11.sp, color = RegalGoldDark)
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "FD Principal: ₹${"%,d".format(fund.fdPrincipalAmount)}", fontSize = 11.sp, color = TextSecondary)
                Text(text = "Actual Interest: ₹${"%,d".format(fund.actualInterestReceivedTotal)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
            }
        }
    }
}

@Composable
fun ReportPaymentRow(payment: PaymentRecordItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = payment.receiptNumber, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AccentBlue)
                Text(text = "To: ${payment.studentName} (${payment.awardTitle})", fontSize = 12.sp, color = TextPrimary)
                Text(text = "${payment.date} • Mode: ${payment.paymentMode} • Ref: ${payment.bankRef}", fontSize = 10.sp, color = TextMuted)
            }
            Text(text = "₹${"%,d".format(payment.amount)}", fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 14.sp)
        }
    }
}

@Composable
fun AnnualFinancialSummaryCard(
    language: AppLanguage,
    awards: List<AwardGrant>,
    funds: List<MemorialFundItem>,
    payments: List<PaymentRecordItem>
) {
    val totalApprovedAwards = awards.filter { it.approvalStatus == AwardApprovalState.APPROVED }.sumOf { it.amount }
    val totalDisbursed = payments.sumOf { it.amount }
    val totalFdPrincipal = funds.sumOf { it.fdPrincipalAmount }
    val totalActualInterest = funds.sumOf { it.actualInterestReceivedTotal }

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = if (language == AppLanguage.GUJARATI) "વાર્ષિક નાણાકીય સારાંશ પત્રક (2024-2025)" else "Annual Financial & Audit Statement",
                fontWeight = FontWeight.Bold,
                color = RoyalNavy,
                fontSize = 15.sp
            )

            SummaryMetricRow("Protected FD Principal Corpus", "₹${"%,d".format(totalFdPrincipal)}", TextPrimary)
            SummaryMetricRow("Actual Bank Interest Received", "₹${"%,d".format(totalActualInterest)}", SuccessGreen)
            SummaryMetricRow("Total Approved Award Grants", "₹${"%,d".format(totalApprovedAwards)}", RegalGoldDark)
            SummaryMetricRow("Disbursed Payments to Students", "₹${"%,d".format(totalDisbursed)}", SuccessGreen)
            SummaryMetricRow("Outstanding Undisbursed Awards", "₹${"%,d".format(totalApprovedAwards - totalDisbursed)}", WarningOrange)
        }
    }
}

@Composable
fun SummaryMetricRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
