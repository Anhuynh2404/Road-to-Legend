package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.ui.screens.CharacterCreationScreen
import com.example.roadtolegend.ui.screens.DashboardScreen
import com.example.roadtolegend.ui.screens.LeagueTableScreen
import com.example.roadtolegend.ui.screens.LifestyleShopScreen
import com.example.roadtolegend.ui.screens.MatchScreen
import com.example.roadtolegend.ui.screens.SaveProfileScreen
import com.example.roadtolegend.ui.screens.TrainingScreen
import com.example.roadtolegend.ui.screens.WelcomeScreen
import com.example.roadtolegend.ui.viewmodel.AppScreen
import com.example.roadtolegend.ui.viewmodel.CareerViewModel
import com.example.roadtolegend.ui.viewmodel.MainTab
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

class MainActivity : ComponentActivity() {

    private val viewModel: CareerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: CareerViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val careerState by viewModel.careerState.collectAsState()
    val shopItems by viewModel.shopItems.collectAsState()
    val saveSlots by viewModel.saveSlots.collectAsState()
    val currentMatch by viewModel.currentMatch.collectAsState()
    val displayedMinute by viewModel.displayedMinute.collectAsState()
    val visibleEvents by viewModel.visibleMatchEvents.collectAsState()
    val isSimulating by viewModel.isSimulatingMatch.collectAsState()
    val statusNotification by viewModel.statusNotification.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusNotification) {
        statusNotification?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearNotification()
        }
    }

    // Xử lý nút Back của hệ thống
    BackHandler(enabled = currentScreen != AppScreen.WELCOME) {
        if (currentScreen == AppScreen.CREATION) {
            viewModel.navigateTo(AppScreen.WELCOME)
        } else if (currentScreen == AppScreen.MAIN_HUB) {
            if (currentTab != MainTab.DASHBOARD) {
                viewModel.selectTab(MainTab.DASHBOARD)
            } else {
                viewModel.navigateTo(AppScreen.WELCOME)
            }
        }
    }

    when (currentScreen) {
        AppScreen.WELCOME -> {
            WelcomeScreen(
                viewModel = viewModel,
                careerState = careerState
            )
        }
        AppScreen.CREATION -> {
            CharacterCreationScreen(
                viewModel = viewModel
            )
        }
        AppScreen.MAIN_HUB -> {
            Scaffold(
                containerColor = DarkBg,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    NavigationBar(
                        containerColor = DarkSurface,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = currentTab == MainTab.DASHBOARD,
                            onClick = { viewModel.selectTab(MainTab.DASHBOARD) },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                            label = { Text("Tổng quan", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DarkBg,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == MainTab.TRAINING,
                            onClick = { viewModel.selectTab(MainTab.TRAINING) },
                            icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Training") },
                            label = { Text("Tập luyện", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DarkBg,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == MainTab.MATCHDAY,
                            onClick = { viewModel.selectTab(MainTab.MATCHDAY) },
                            icon = { Icon(Icons.Default.SportsSoccer, contentDescription = "Matchday") },
                            label = { Text("Trận đấu", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DarkBg,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == MainTab.SHOP,
                            onClick = { viewModel.selectTab(MainTab.SHOP) },
                            icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Shop") },
                            label = { Text("Cửa hàng", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DarkBg,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == MainTab.LEAGUE_TABLE,
                            onClick = { viewModel.selectTab(MainTab.LEAGUE_TABLE) },
                            icon = { Icon(Icons.Default.Leaderboard, contentDescription = "League") },
                            label = { Text("BXH", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DarkBg,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == MainTab.PROFILE_SAVE,
                            onClick = { viewModel.selectTab(MainTab.PROFILE_SAVE) },
                            icon = { Icon(Icons.Default.Save, contentDescription = "Profile") },
                            label = { Text("Hồ sơ", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DarkBg,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    val state = careerState
                    if (state != null) {
                        when (currentTab) {
                            MainTab.DASHBOARD -> DashboardScreen(careerState = state, viewModel = viewModel)
                            MainTab.TRAINING -> TrainingScreen(careerState = state, viewModel = viewModel)
                            MainTab.MATCHDAY -> MatchScreen(
                                careerState = state,
                                viewModel = viewModel,
                                currentMatch = currentMatch,
                                displayedMinute = displayedMinute,
                                visibleEvents = visibleEvents,
                                isSimulating = isSimulating
                            )
                            MainTab.SHOP -> LifestyleShopScreen(careerState = state, shopItems = shopItems, viewModel = viewModel)
                            MainTab.LEAGUE_TABLE -> LeagueTableScreen(careerState = state)
                            MainTab.PROFILE_SAVE -> SaveProfileScreen(careerState = state, saveSlots = saveSlots, viewModel = viewModel)
                        }
                    } else {
                        SaveProfileScreen(careerState = null, saveSlots = saveSlots, viewModel = viewModel)
                    }
                }
            }
        }
    }
}
