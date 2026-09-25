package com.example.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.tabs.AiScientistTab
import com.example.ui.tabs.BrainTab
import com.example.ui.tabs.BrowserTab
import com.example.ui.tabs.DashboardTab
import com.example.ui.tabs.ForexArbitrageTab
import com.example.ui.tabs.LiveVisionAudioTab
import com.example.ui.tabs.MouseArenaTab
import com.example.ui.tabs.OverlayTab
import com.example.ui.tabs.ProfilesTab
import com.example.ui.tabs.QuantumCouncilTab
import com.example.ui.tabs.ScreenVisionTab
import com.example.ui.tabs.SmartVisualTriggerTab
import com.example.viewmodel.AppTab
import com.example.viewmodel.AutomationStatus
import com.example.viewmodel.MainUiEffect
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val dashboardListState = rememberLazyListState()
    val context = LocalContext.current
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(Vibrator::class.java)
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is MainUiEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is MainUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is MainUiEffect.TriggerHaptic -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(
                            VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE)
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(25)
                    }
                }
                is MainUiEffect.ScrollToLatestLog -> {
                    if (uiState.logs.isNotEmpty()) {
                        dashboardListState.animateScrollToItem(0)
                    }
                }
                is MainUiEffect.RequestOverlayPermission -> {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                }
                is MainUiEffect.OpenAccessibilitySettings -> {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                }
                is MainUiEffect.ClickImpactPulse -> {
                    // Handled reactively via state
                }
                is MainUiEffect.ExecuteBrowserJs -> {
                    // Handled directly inside BrowserTab
                }
            }
        }
    }

    val isAiSubTab = uiState.selectedTab in listOf(
        AppTab.QUANTUM_COUNCIL,
        AppTab.FOREX_ARBITRAGE,
        AppTab.SCREEN_VISION,
        AppTab.MOUSE_ARENA,
        AppTab.BROWSER,
        AppTab.BRAIN,
        AppTab.AI_SCIENTIST,
        AppTab.LIVE_CAMERA_VOICE
    )

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("main_screen_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "1000 CPS MS Clone",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF)),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    },
                    actions = {
                        // Quick Emergency Stop Kill Switch Button
                        Button(
                            onClick = { viewModel.emergencyStopAll() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("topbar_emergency_stop_button")
                        ) {
                            Icon(
                                Icons.Default.Stop,
                                contentDescription = "Parar Tudo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = "PARAR",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, color = Color.White)
                            )
                        }

                        // Live Status Badge
                        val badgeColor = when (uiState.status) {
                            AutomationStatus.IDLE -> Color(0xFF94A3B8)
                            AutomationStatus.RUNNING -> Color(0xFF10B981)
                            AutomationStatus.PAUSED -> Color(0xFFF59E0B)
                        }
                        val badgeText = when (uiState.status) {
                            AutomationStatus.IDLE -> "Ocioso"
                            AutomationStatus.RUNNING -> "Ativo"
                            AutomationStatus.PAUSED -> "Pausado"
                        }

                        Surface(
                            color = badgeColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (uiState.isOverlayServiceRunning) "HUD Flutuante ($badgeText)" else badgeText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = badgeColor
                                    )
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Sub-navigation bar when user is inside AI or Council modules
                if (isAiSubTab) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = uiState.selectedTab == AppTab.LIVE_CAMERA_VOICE,
                            onClick = { viewModel.setTab(AppTab.LIVE_CAMERA_VOICE) },
                            label = { Text("🔴 Live Câmera & Voz", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        FilterChip(
                            selected = uiState.selectedTab == AppTab.QUANTUM_COUNCIL,
                            onClick = { viewModel.setTab(AppTab.QUANTUM_COUNCIL) },
                            label = { Text("⚛️ Sala Quântica", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = uiState.selectedTab == AppTab.FOREX_ARBITRAGE,
                            onClick = { viewModel.setTab(AppTab.FOREX_ARBITRAGE) },
                            label = { Text("💱 Câmbio Wise", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = uiState.selectedTab == AppTab.SCREEN_VISION,
                            onClick = { viewModel.setTab(AppTab.SCREEN_VISION) },
                            label = { Text("👁️ Visão IA", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = uiState.selectedTab == AppTab.MOUSE_ARENA,
                            onClick = { viewModel.setTab(AppTab.MOUSE_ARENA) },
                            label = { Text("🎯 Mira & Mouse", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = uiState.selectedTab == AppTab.BROWSER,
                            onClick = { viewModel.setTab(AppTab.BROWSER) },
                            label = { Text("🌐 Chrome Autônomo", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = uiState.selectedTab == AppTab.BRAIN,
                            onClick = { viewModel.setTab(AppTab.BRAIN) },
                            label = { Text("🧠 Cérebro", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = uiState.selectedTab == AppTab.AI_SCIENTIST,
                            onClick = { viewModel.setTab(AppTab.AI_SCIENTIST) },
                            label = { Text("🔬 Cientista", fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("app_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                // Tab 1: Turbo Clicker Panel
                NavigationBarItem(
                    selected = uiState.selectedTab == AppTab.CONTROLS,
                    onClick = { viewModel.setTab(AppTab.CONTROLS) },
                    icon = { Icon(Icons.Default.Bolt, contentDescription = "Painel Turbo 1000 CPS") },
                    label = { Text("Turbo 1000", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.testTag("nav_tab_controls")
                )

                // Tab 2: Smart Visual Triggers & Macros
                NavigationBarItem(
                    selected = uiState.selectedTab == AppTab.VISUAL_TRIGGERS,
                    onClick = { viewModel.setTab(AppTab.VISUAL_TRIGGERS) },
                    icon = { Icon(Icons.Default.Radar, contentDescription = "Gatilhos e Macros") },
                    label = { Text("Gatilhos", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.testTag("nav_tab_visual_triggers")
                )

                // Tab 3: Unlimited Profiles (Microsoft Store)
                NavigationBarItem(
                    selected = uiState.selectedTab == AppTab.PROFILES,
                    onClick = { viewModel.setTab(AppTab.PROFILES) },
                    icon = { Icon(Icons.Default.Widgets, contentDescription = "Perfis Ilimitados") },
                    label = { Text("Perfis", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.testTag("nav_tab_profiles")
                )

                // Tab 4: Windows 11 Glassmorphism HUD Overlay
                NavigationBarItem(
                    selected = uiState.selectedTab == AppTab.OVERLAY,
                    onClick = { viewModel.setTab(AppTab.OVERLAY) },
                    icon = { Icon(Icons.Default.Layers, contentDescription = "Sobreposição Flutuante") },
                    label = { Text("Sobrepor", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.testTag("nav_tab_overlay")
                )

                // Tab 5: Quantum Council & AI Hub
                NavigationBarItem(
                    selected = isAiSubTab,
                    onClick = { viewModel.setTab(AppTab.QUANTUM_COUNCIL) },
                    icon = { Icon(Icons.Default.Psychology, contentDescription = "IA Quântica") },
                    label = { Text("IA Quântica", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.testTag("nav_tab_quantum_council")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.selectedTab) {
                AppTab.CONTROLS -> {
                    DashboardTab(
                        uiState = uiState,
                        viewModel = viewModel,
                        listState = dashboardListState
                    )
                }
                AppTab.VISUAL_TRIGGERS -> {
                    SmartVisualTriggerTab(
                        state = uiState,
                        viewModel = viewModel
                    )
                }
                AppTab.PROFILES -> {
                    ProfilesTab(
                        state = uiState,
                        viewModel = viewModel
                    )
                }
                AppTab.OVERLAY -> {
                    OverlayTab(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
                AppTab.QUANTUM_COUNCIL -> {
                    QuantumCouncilTab(
                        state = uiState,
                        onToggleMeeting = { viewModel.toggleQuantumMeeting() },
                        onAddDoctor = { name, role, formula -> viewModel.manuallyRecruitDoctor(name, role, formula) }
                    )
                }
                AppTab.FOREX_ARBITRAGE -> {
                    ForexArbitrageTab(
                        state = uiState,
                        onSelectPair = { viewModel.selectCurrencyPair(it) },
                        onExecuteArbitrage = { viewModel.executeArbitrageConversion(it) },
                        onToggleAutonomousPatrol = { viewModel.toggleAutonomousPatrol() },
                        onClearSignals = { viewModel.clearArbitrageSignals() }
                    )
                }
                AppTab.SCREEN_VISION -> {
                    ScreenVisionTab(
                        state = uiState,
                        onScanScreen = { viewModel.scanActiveScreen() },
                        onMoveMouseToElement = { viewModel.moveMouseToElement(it, performClick = true) },
                        onOpenAccessibility = { viewModel.openAccessibilitySettings() },
                        onClearScreenHistory = { viewModel.clearScreenMemories() }
                    )
                }
                AppTab.MOUSE_ARENA -> {
                    MouseArenaTab(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
                AppTab.BROWSER -> {
                    BrowserTab(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
                AppTab.BRAIN -> {
                    BrainTab(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
                AppTab.AI_SCIENTIST -> {
                    AiScientistTab(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
                AppTab.LIVE_CAMERA_VOICE -> {
                    LiveVisionAudioTab(
                        state = uiState,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
