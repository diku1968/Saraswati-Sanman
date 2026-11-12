package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.AppLanguage
import com.example.domain.AppStrings
import com.example.domain.Role
import com.example.ui.SaraswatiViewModel
import com.example.ui.ads.AdMobBanner
import com.example.ui.ads.AdMobManager
import com.example.ui.components.AppTopHeader
import com.example.ui.screens.*
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.SaraswatiSammanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Google Mobile Ads (AdMob)
        AdMobManager.initialize(this)

        enableEdgeToEdge()
        setContent {
            SaraswatiSammanTheme {
                SaraswatiApp()
            }
        }
    }
}

@Composable
fun SaraswatiApp(viewModel: SaraswatiViewModel = viewModel()) {
    val language by viewModel.currentLanguage.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val activeSociety by viewModel.activeSociety.collectAsState()
    val societies by viewModel.societies.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var currentNavDestination by remember { mutableStateOf("dashboard") }

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg.message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AppTopHeader(
                currentLanguage = language,
                currentRole = currentRole,
                activeSociety = activeSociety,
                societies = societies,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onSelectRole = { viewModel.setRole(it) },
                onSelectSociety = { viewModel.selectSociety(it) }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                // AdMob Banner Ad (Test Ad Unit)
                AdMobBanner(
                    modifier = Modifier.fillMaxWidth(),
                    language = language
                )

                NavigationBar(
                    containerColor = androidx.compose.ui.graphics.Color.White,
                    tonalElevation = 4.dp,
                    modifier = Modifier.testTag("app_bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentNavDestination == "dashboard",
                        onClick = { currentNavDestination = "dashboard" },
                        icon = {
                            Icon(
                                imageVector = when (currentRole) {
                                    Role.STUDENT -> Icons.Default.School
                                    Role.SOCIETY_ADMIN -> Icons.Default.AdminPanelSettings
                                    Role.COMMITTEE_MEMBER -> Icons.Default.AssignmentTurnedIn
                                    Role.TREASURER -> Icons.Default.Savings
                                    Role.DONOR -> Icons.Default.VolunteerActivism
                                    Role.SUPER_ADMIN -> Icons.Default.Security
                                },
                                contentDescription = "Dashboard"
                            )
                        },
                        label = {
                            Text(
                                text = when (currentRole) {
                                    Role.STUDENT -> if (language == AppLanguage.GUJARATI) "વિદ્યાર્થી" else "Student"
                                    Role.SOCIETY_ADMIN -> if (language == AppLanguage.GUJARATI) "પ્રશાસક" else "Admin"
                                    Role.COMMITTEE_MEMBER -> if (language == AppLanguage.GUJARATI) "સમિતિ" else "Committee"
                                    Role.TREASURER -> if (language == AppLanguage.GUJARATI) "તિજોરી" else "Treasurer"
                                    Role.DONOR -> if (language == AppLanguage.GUJARATI) "દાતાશ્રી" else "Donor"
                                    Role.SUPER_ADMIN -> if (language == AppLanguage.GUJARATI) "સુપર એડમિન" else "Super Admin"
                                },
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        modifier = Modifier.testTag("nav_item_dashboard")
                    )

                    NavigationBarItem(
                        selected = currentNavDestination == "reports",
                        onClick = { currentNavDestination = "reports" },
                        icon = { Icon(Icons.Default.Summarize, contentDescription = "Reports") },
                        label = {
                            Text(
                                text = if (language == AppLanguage.GUJARATI) "અહેવાલો" else "Reports",
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        modifier = Modifier.testTag("nav_item_reports")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (currentNavDestination == "reports") {
                ReportsScreen(viewModel = viewModel)
            } else {
                when (currentRole) {
                    Role.STUDENT -> StudentDashboardScreen(viewModel = viewModel)
                    Role.SOCIETY_ADMIN -> AdminDashboardScreen(viewModel = viewModel)
                    Role.COMMITTEE_MEMBER -> CommitteeDashboardScreen(viewModel = viewModel)
                    Role.TREASURER -> TreasurerDashboardScreen(viewModel = viewModel)
                    Role.DONOR -> DonorDashboardScreen(viewModel = viewModel)
                    Role.SUPER_ADMIN -> SuperAdminDashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}

