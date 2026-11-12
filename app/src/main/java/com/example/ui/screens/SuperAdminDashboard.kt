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
fun SuperAdminDashboardScreen(
    viewModel: SaraswatiViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val societies by viewModel.societies.collectAsState()
    val activeSocietyId by viewModel.selectedSocietyId.collectAsState()

    var showCreateSocietyDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(SurfaceWhite).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = RoyalNavy),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("super_admin_header_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.GUJARATI) "પ્લેટફોર્મ સુપર એડમિન કંટ્રોલ" else "Platform Multi-Tenant Governance",
                                color = RegalGoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${societies.size} Registered Samaj Trusts",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(RegalGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = RoyalNavy)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (language == AppLanguage.GUJARATI)
                            "કડક ટેનન્ટ આઇસોલેશન: એક સમાજનો ડેટા બીજા સમાજથી સંપૂર્ણ સુરક્ષિત અને અલગ રાખવામાં આવે છે."
                            else "Zero Cross-Tenant Leakage: Enforces strict society-level tenant isolation across all database operations.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.GUJARATI) "નોંધાયેલા સમાજ અને મંડળો" else "Registered Samaj Societies",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RoyalNavy)
                )

                Button(
                    onClick = { showCreateSocietyDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_society_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (language == AppLanguage.GUJARATI) "નવો સમાજ ઉમેરો" else "Add Samaj")
                }
            }
        }

        items(societies) { soc ->
            val isCurrent = soc.id == activeSocietyId
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) GoldContainer else SurfaceCard
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("society_item_card_${soc.code}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == AppLanguage.GUJARATI) soc.nameGujarati else soc.name,
                                fontWeight = FontWeight.Bold,
                                color = RoyalNavy,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Code: ${soc.code} • Reg: ${soc.registrationNumber}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Surface(
                            color = if (soc.isActive) SuccessBg else ErrorBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (soc.isActive) "ACTIVE" else "SUSPENDED",
                                color = if (soc.isActive) SuccessGreen else ErrorRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Address: ${soc.address}", fontSize = 11.sp, color = TextMuted)
                    Text(text = "Email: ${soc.contactEmail} • Phone: ${soc.contactPhone}", fontSize = 11.sp, color = TextMuted)

                    if (!isCurrent) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { viewModel.selectSociety(soc.id) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("switch_to_society_${soc.code}")
                        ) {
                            Text(if (language == AppLanguage.GUJARATI) "આ સમાજ પસંદ કરો (Switch Tenant)" else "Switch to this Samaj Tenant")
                        }
                    }
                }
            }
        }
    }

    if (showCreateSocietyDialog) {
        AlertDialog(
            onDismissRequest = { showCreateSocietyDialog = false },
            title = { Text("Add New Samaj Society", fontWeight = FontWeight.Bold, color = RoyalNavy) },
            text = {
                Text("Form allows Super Admin to onboard a new educational trust with tenant isolation.")
            },
            confirmButton = {
                Button(
                    onClick = { showCreateSocietyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                ) {
                    Text("Close")
                }
            }
        )
    }
}
