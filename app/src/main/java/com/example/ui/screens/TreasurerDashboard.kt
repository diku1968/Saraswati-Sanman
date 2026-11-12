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
fun TreasurerDashboardScreen(
    viewModel: SaraswatiViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val memorialFunds by viewModel.memorialFunds.collectAsState()
    val awards by viewModel.awards.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val payments by viewModel.payments.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showInterestCreditDialog by remember { mutableStateOf(false) }
    var disbursingAward by remember { mutableStateOf<AwardGrant?>(null) }
    var reversingTransaction by remember { mutableStateOf<FinancialTransactionItem?>(null) }

    Column(modifier = modifier.fillMaxSize().background(SurfaceWhite)) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = RoyalNavy,
            edgePadding = 12.dp
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(if (language == AppLanguage.GUJARATI) "FD અને સ્મૃતિ ફંડ" else "FDs & Memorial Funds", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Savings, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(if (language == AppLanguage.GUJARATI) "ચુકવણી વિતરણ" else "Award Disbursements", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Payments, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text(if (language == AppLanguage.GUJARATI) "નાણાકીય વ્યવહાર ખાતાવહી" else "Transactions & Reversals", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) }
            )
        }

        when (selectedTab) {
            0 -> FdAndMemorialTab(
                language = language,
                funds = memorialFunds,
                onCreditInterestClick = { showInterestCreditDialog = true }
            )
            1 -> AwardDisbursementTab(
                language = language,
                awards = awards.filter { it.approvalStatus == AwardApprovalState.APPROVED },
                onDisburseClick = { disbursingAward = it }
            )
            2 -> TransactionsLedgerTab(
                language = language,
                transactions = transactions,
                onReverseClick = { reversingTransaction = it }
            )
        }
    }

    if (showInterestCreditDialog && memorialFunds.isNotEmpty()) {
        CreditFdInterestDialog(
            language = language,
            funds = memorialFunds,
            onDismiss = { showInterestCreditDialog = false },
            onSubmit = { fundId, amount, ref, narr ->
                viewModel.recordFdInterest(fundId, amount, ref, narr)
                showInterestCreditDialog = false
            }
        )
    }

    if (disbursingAward != null) {
        DisbursePaymentDialog(
            language = language,
            award = disbursingAward!!,
            onDismiss = { disbursingAward = null },
            onSubmit = { mode, ref, notes ->
                viewModel.disburseAwardPayment(disbursingAward!!.id, mode, ref, notes)
                disbursingAward = null
            }
        )
    }

    if (reversingTransaction != null) {
        ReverseTransactionDialog(
            language = language,
            tx = reversingTransaction!!,
            onDismiss = { reversingTransaction = null },
            onSubmit = { reason ->
                viewModel.reverseTransaction(reversingTransaction!!.id, reason)
                reversingTransaction = null
            }
        )
    }
}

@Composable
fun FdAndMemorialTab(
    language: AppLanguage,
    funds: List<MemorialFundItem>,
    onCreditInterestClick: () -> Unit
) {
    val totalPrincipal = funds.sumOf { it.fdPrincipalAmount }
    val totalActualInterest = funds.sumOf { it.actualInterestReceivedTotal }
    val totalAvailable = funds.sumOf { it.availableBalance }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Summary Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = RoyalNavy),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("fd_summary_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (language == AppLanguage.GUJARATI) "તિજોરી ભંડોળ સારાંશ (કડક સિદ્ધાંત)" else "Treasury & FD Integrity Ledger",
                        color = RegalGoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.GUJARATI) "FD મુદ્દલ (સુરક્ષિત)" else "FD Principal (Protected)",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "₹${"%,d".format(totalPrincipal)}",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (language == AppLanguage.GUJARATI) "વાસ્તવિક પ્રાપ્ત વ્યાજ" else "Actual Interest Received",
                                color = RegalGoldLight,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "₹${"%,d".format(totalActualInterest)}",
                                color = RegalGold,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.GUJARATI) "પુરસ્કાર માટે ઉપલબ્ધ સિલક:" else "Available for Awards:",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "₹${"%,d".format(totalAvailable)}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Strict Separation Notice
        item {
            Surface(
                color = GoldContainer,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, RegalGold.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = RegalGoldDark, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.GUJARATI)
                            "મહત્વપૂર્ણ નાણાકીય નિયમ: FD મુદ્દલ ક્યારેય ખર્ચવામાં આવતી નથી. માત્ર બેંક ખાતામાં જમા થયેલ વાસ્તવિક વ્યાજ જ પુરસ્કાર માટે ફાળવાય છે (અંદાજિત વ્યાજ નહીં)."
                            else "Financial Rule: FD principal is NEVER spent. Only bank-supported actual interest counts as received (never estimated).",
                        fontSize = 12.sp,
                        color = OnGoldContainer,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Action Button: Credit Actual FD Interest
        item {
            Button(
                onClick = onCreditInterestClick,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("credit_actual_fd_interest_button")
            ) {
                Icon(Icons.Default.AddCard, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.GUJARATI) "બેંક વ્યાજ જમા કરો (Credit Actual Interest)" else "Credit Actual Bank FD Interest",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Memorial Funds List
        items(funds) { fund ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("memorial_fund_card_${fund.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.GUJARATI) fund.fundNameGujarati else fund.fundName,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "In Memory Of: ${fund.inMemoryOf}",
                        fontSize = 12.sp,
                        color = RegalGoldDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Bank: ${fund.bankName} • FD Cert: ${fund.fdCertificateNumber}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "FD Principal", fontSize = 11.sp, color = TextMuted)
                            Text(text = "₹${"%,d".format(fund.fdPrincipalAmount)}", fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column {
                            Text(text = "Actual Interest Recd", fontSize = 11.sp, color = TextMuted)
                            Text(text = "₹${"%,d".format(fund.actualInterestReceivedTotal)}", fontWeight = FontWeight.Bold, color = SuccessGreen)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Available Balance", fontSize = 11.sp, color = TextMuted)
                            Text(text = "₹${"%,d".format(fund.availableBalance)}", fontWeight = FontWeight.Bold, color = RoyalNavy)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AwardDisbursementTab(
    language: AppLanguage,
    awards: List<AwardGrant>,
    onDisburseClick: (AwardGrant) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(awards) { award ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().testTag("disbursement_award_card_${award.id}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == AppLanguage.GUJARATI) award.awardTitleGujarati else award.awardTitle,
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavy,
                            fontSize = 14.sp
                        )
                        Text(text = "Student: ${award.studentName}", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "Amount: ₹${"%,d".format(award.amount)}", fontWeight = FontWeight.SemiBold, color = RegalGoldDark)
                        if (award.receiptNumber != null) {
                            Text(text = "Receipt: ${award.receiptNumber} (${award.paymentMode})", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    if (award.paymentStatus == PaymentState.PAID) {
                        Surface(color = SuccessBg, shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = if (language == AppLanguage.GUJARATI) "ચુકવાઈ ગયું" else "PAID",
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { onDisburseClick(award) },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("disburse_button_${award.id}")
                        ) {
                            Text(if (language == AppLanguage.GUJARATI) "ચુકવણી કરો" else "Disburse")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionsLedgerTab(
    language: AppLanguage,
    transactions: List<FinancialTransactionItem>,
    onReverseClick: (FinancialTransactionItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(transactions) { tx ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (tx.isReversed) SurfaceSubtle else SurfaceCard
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().testTag("tx_card_${tx.id}")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = tx.referenceNumber, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AccentBlue)
                            if (tx.isReversed) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(color = ErrorBg, shape = RoundedCornerShape(4.dp)) {
                                    Text(text = "REVERSED", color = ErrorRed, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                                }
                            }
                        }
                        Text(text = tx.narrative, fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${tx.date} • Type: ${tx.transactionType}", fontSize = 11.sp, color = TextMuted)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${if (tx.amount >= 0) "+" else ""}₹${"%,d".format(tx.amount)}",
                            fontWeight = FontWeight.Bold,
                            color = if (tx.amount >= 0) SuccessGreen else ErrorRed,
                            fontSize = 14.sp
                        )

                        if (!tx.isReversed && tx.transactionType != "REVERSAL") {
                            TextButton(
                                onClick = { onReverseClick(tx) },
                                modifier = Modifier.testTag("reverse_button_${tx.id}")
                            ) {
                                Text(if (language == AppLanguage.GUJARATI) "રદ કરો" else "Reverse", fontSize = 11.sp, color = ErrorRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreditFdInterestDialog(
    language: AppLanguage,
    funds: List<MemorialFundItem>,
    onDismiss: () -> Unit,
    onSubmit: (String, Long, String, String) -> Unit
) {
    var selectedFundId by remember { mutableStateOf(funds.first().id) }
    var interestAmount by remember { mutableStateOf("36250") }
    var bankRef by remember { mutableStateOf("BOB-INT-2024-9981") }
    var narrative by remember { mutableStateOf("Actual bank interest credited for annual award sponsorship") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (language == AppLanguage.GUJARATI) "વાસ્તવિક બેંક વ્યાજ જમા લોગ" else "Record Actual Bank FD Interest",
                fontWeight = FontWeight.Bold,
                color = RoyalNavy
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (language == AppLanguage.GUJARATI)
                        "માત્ર બેંક પાસબુક/સ્ટેટમેન્ટમાં ખરેખર જમા થયેલ વ્યાજ જ દાખલ કરો."
                        else "Only enter bank-credited actual interest verified by statement.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = interestAmount,
                    onValueChange = { interestAmount = it },
                    label = { Text(if (language == AppLanguage.GUJARATI) "વ્યાજ રકમ (રૂ.)" else "Interest Amount (INR)") },
                    modifier = Modifier.fillMaxWidth().testTag("credit_interest_amount_input")
                )
                OutlinedTextField(
                    value = bankRef,
                    onValueChange = { bankRef = it },
                    label = { Text("Bank Statement Ref / UTR") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = narrative,
                    onValueChange = { narrative = it },
                    label = { Text("Narrative / Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = interestAmount.toLongOrNull() ?: 0L
                    if (amount > 0) {
                        onSubmit(selectedFundId, amount, bankRef, narrative)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                modifier = Modifier.testTag("confirm_credit_interest_button")
            ) {
                Text(if (language == AppLanguage.GUJARATI) "જમા કરો" else "Record Credit")
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
fun DisbursePaymentDialog(
    language: AppLanguage,
    award: AwardGrant,
    onDismiss: () -> Unit,
    onSubmit: (PaymentChannel, String, String) -> Unit
) {
    var mode by remember { mutableStateOf(PaymentChannel.BANK_TRANSFER) }
    var bankRef by remember { mutableStateOf("NEFT-BOB-984212") }
    var notes by remember { mutableStateOf("Annual Education Award ceremony prize disbursement") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Disburse Award Payment", fontWeight = FontWeight.Bold, color = RoyalNavy)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Student: ${award.studentName}", fontWeight = FontWeight.Bold)
                Text(text = "Award: ${award.awardTitle}", color = RegalGoldDark)
                Text(text = "Amount: ₹${"%,d".format(award.amount)}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Payment Mode:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PaymentChannel.values().forEach { ch ->
                        FilterChip(
                            selected = mode == ch,
                            onClick = { mode = ch },
                            label = { Text(ch.name.replace("_", " "), fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = bankRef,
                    onValueChange = { bankRef = it },
                    label = { Text("Transaction / Cheque / Voucher Ref") },
                    modifier = Modifier.fillMaxWidth().testTag("payment_ref_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(mode, bankRef, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                modifier = Modifier.testTag("confirm_disburse_button")
            ) {
                Text("Disburse Payment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ReverseTransactionDialog(
    language: AppLanguage,
    tx: FinancialTransactionItem,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var reason by remember { mutableStateOf("Data entry typo correction") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Reverse Mistake Transaction", fontWeight = FontWeight.Bold, color = ErrorRed)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Reference: ${tx.referenceNumber}", fontWeight = FontWeight.SemiBold)
                Text(text = "Amount: ₹${tx.amount} (${tx.narrative})", fontSize = 12.sp)
                Text(
                    text = "Accounting Rule: Reversals create an immutable counter-entry preserving full audit history.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for Reversal") },
                    modifier = Modifier.fillMaxWidth().testTag("reversal_reason_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(reason) },
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                modifier = Modifier.testTag("confirm_reverse_button")
            ) {
                Text("Confirm Reversal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
