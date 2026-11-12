package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.AppLanguage
import com.example.domain.AppStrings
import com.example.domain.Role
import com.example.domain.SocietyItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopHeader(
    currentLanguage: AppLanguage,
    currentRole: Role,
    activeSociety: SocietyItem?,
    societies: List<SocietyItem>,
    onToggleLanguage: () -> Unit,
    onSelectRole: (Role) -> Unit,
    onSelectSociety: (String) -> Unit
) {
    var showSocietyMenu by remember { mutableStateOf(false) }
    var showRoleMenu by remember { mutableStateOf(false) }

    Surface(
        color = RoyalNavy,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Main Branding Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Emblem circle badge
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(RegalGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Emblem",
                            tint = RoyalNavy,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = AppStrings.get("app_title", currentLanguage),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = AppStrings.get("app_subtitle", currentLanguage),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = RegalGoldLight
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Language Switch Chip
                ElevatedFilterChip(
                    selected = true,
                    onClick = onToggleLanguage,
                    label = {
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "🇬🇺 ગુજરાતી" else "🇬🇧 English",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = RoyalNavy
                        )
                    },
                    colors = FilterChipDefaults.elevatedFilterChipColors(
                        selectedContainerColor = RegalGold,
                        selectedLabelColor = RoyalNavy
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("language_toggle_chip")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Multi-Samaj Society & Role Switcher Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Active Samaj Dropdown
                Box(modifier = Modifier.weight(1.3f)) {
                    Surface(
                        color = Color(0xFF162D55),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSocietyMenu = true }
                            .testTag("society_selector_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = "Samaj",
                                tint = RegalGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (currentLanguage == AppLanguage.GUJARATI) {
                                    activeSociety?.nameGujarati ?: activeSociety?.name ?: "સમાજ પસંદ કરો"
                                } else {
                                    activeSociety?.name ?: "Select Samaj"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showSocietyMenu,
                        onDismissRequest = { showSocietyMenu = false }
                    ) {
                        societies.forEach { soc ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = if (currentLanguage == AppLanguage.GUJARATI) soc.nameGujarati else soc.name,
                                            fontWeight = if (soc.id == activeSociety?.id) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = "Code: ${soc.code} • Reg: ${soc.registrationNumber}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                    }
                                },
                                onClick = {
                                    onSelectSociety(soc.id)
                                    showSocietyMenu = false
                                }
                            )
                        }
                    }
                }

                // Active Role Dropdown
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = RegalGoldDark,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showRoleMenu = true }
                            .testTag("role_selector_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = "Role",
                                tint = RegalGoldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (currentRole) {
                                    Role.STUDENT -> AppStrings.get("role_student", currentLanguage)
                                    Role.SOCIETY_ADMIN -> AppStrings.get("role_society_admin", currentLanguage)
                                    Role.COMMITTEE_MEMBER -> AppStrings.get("role_committee", currentLanguage)
                                    Role.TREASURER -> AppStrings.get("role_treasurer", currentLanguage)
                                    Role.DONOR -> AppStrings.get("role_donor", currentLanguage)
                                    Role.SUPER_ADMIN -> AppStrings.get("role_super_admin", currentLanguage)
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showRoleMenu,
                        onDismissRequest = { showRoleMenu = false }
                    ) {
                        Role.values().forEach { roleOption ->
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (roleOption) {
                                            Role.STUDENT -> Icons.Default.School
                                            Role.SOCIETY_ADMIN -> Icons.Default.AdminPanelSettings
                                            Role.COMMITTEE_MEMBER -> Icons.Default.AssignmentTurnedIn
                                            Role.TREASURER -> Icons.Default.Savings
                                            Role.DONOR -> Icons.Default.VolunteerActivism
                                            Role.SUPER_ADMIN -> Icons.Default.Security
                                        },
                                        contentDescription = null,
                                        tint = RoyalNavy
                                    )
                                },
                                text = {
                                    Text(
                                        text = when (roleOption) {
                                            Role.STUDENT -> AppStrings.get("role_student", currentLanguage)
                                            Role.SOCIETY_ADMIN -> AppStrings.get("role_society_admin", currentLanguage)
                                            Role.COMMITTEE_MEMBER -> AppStrings.get("role_committee", currentLanguage)
                                            Role.TREASURER -> AppStrings.get("role_treasurer", currentLanguage)
                                            Role.DONOR -> AppStrings.get("role_donor", currentLanguage)
                                            Role.SUPER_ADMIN -> AppStrings.get("role_super_admin", currentLanguage)
                                        },
                                        fontWeight = if (roleOption == currentRole) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onSelectRole(roleOption)
                                    showRoleMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
