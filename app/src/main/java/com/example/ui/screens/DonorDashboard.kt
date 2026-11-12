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
fun DonorDashboardScreen(
    viewModel: SaraswatiViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val donors by viewModel.donors.collectAsState()
    val memorialFunds by viewModel.memorialFunds.collectAsState()
    val awards by viewModel.awards.collectAsState()

    val currentDonor = donors.firstOrNull()
    val donorFunds = memorialFunds.filter { it.donorId == (currentDonor?.id ?: "") }
    val sponsoredAwards = awards.filter { it.memorialFundName != null }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(SurfaceWhite).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Donor Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = RoyalNavy),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("donor_profile_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.GUJARATI) "દાતાશ્રી પ્રોફાઇલ અને ભંડોળ" else "Donor Endowments & Impact",
                                color = RegalGoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == AppLanguage.GUJARATI && currentDonor?.nameGujarati?.isNotEmpty() == true) 
                                    currentDonor.nameGujarati 
                                    else currentDonor?.name ?: "Shri Pravinchandra Patel",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val contact = currentDonor?.contactNumber ?: "N/A"
                            Text(
                                text = "Contact: $contact",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(RegalGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(32.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Total Contribution", color = RegalGoldLight, fontSize = 11.sp)
                            Text(
                                text = "₹${"%,d".format(currentDonor?.totalContributed ?: 500000L)}",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Memorial Funds", color = RegalGoldLight, fontSize = 11.sp)
                            Text(
                                text = "${donorFunds.size} Dedicated Fund(s)",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Dedicated Memorial Fund (In Memory of Loved Elders)
        item {
            Text(
                text = if (language == AppLanguage.GUJARATI) "સ્વજનોની સ્મૃતિમાં સમર્પિત કાયમી એવોર્ડ ભંડોળ" else "Endowed Memorial Funds (In Memory of Elders)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RoyalNavy)
            )
        }

        items(donorFunds) { fund ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.GUJARATI) fund.fundNameGujarati else fund.fundName,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Dedicated: ${fund.inMemoryOf}",
                        color = RegalGoldDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Bank FD: ${fund.bankName} (Cert: ${fund.fdCertificateNumber})",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = GoldContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Protected Principal", fontSize = 11.sp, color = TextSecondary)
                                Text(text = "₹${"%,d".format(fund.fdPrincipalAmount)}", fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Column {
                                Text(text = "Actual Bank Interest", fontSize = 11.sp, color = TextSecondary)
                                Text(text = "₹${"%,d".format(fund.actualInterestReceivedTotal)}", fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Disbursed to Students", fontSize = 11.sp, color = TextSecondary)
                                Text(text = "₹${"%,d".format(fund.allocatedAmount)}", fontWeight = FontWeight.Bold, color = RoyalNavy)
                            }
                        }
                    }
                }
            }
        }

        // Privacy-Respecting Student Impact Statement
        item {
            Text(
                text = if (language == AppLanguage.GUJARATI) "પુરસ્કાર પ્રભાવ અને વિદ્યાર્થી સન્માન" else "Privacy-Respecting Student Impact",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RoyalNavy)
            )
        }

        items(sponsoredAwards) { aw ->
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
                        Text(text = aw.awardTitle, fontWeight = FontWeight.Bold, color = RoyalNavy, fontSize = 14.sp)
                        Text(text = "Beneficiary: ${aw.studentName} (${aw.academicYear})", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "Evidence: ${aw.eligibilityEvidence}", fontSize = 11.sp, color = TextSecondary)
                    }

                    Surface(color = GoldContainer, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            text = "₹${"%,d".format(aw.amount)}",
                            color = OnGoldContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
