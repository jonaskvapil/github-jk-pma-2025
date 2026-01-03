package com.example.investdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.investdiary.datastore.PreferencesManager
import com.example.investdiary.navigation.Screen
import com.example.investdiary.screens.AddInvestmentScreen
import com.example.investdiary.screens.InvestmentDetailScreen
import com.example.investdiary.screens.PortfolioScreen
import com.example.investdiary.screens.SettingsScreen
import com.example.investdiary.ui.theme.InvestDiaryTheme
import com.example.investdiary.viewmodel.InvestmentViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Načtení nastavení tématu (Dark/Light mode)
        val preferencesManager = PreferencesManager(applicationContext)

        setContent {
            // Sledujeme změnu tématu
            val isDarkTheme by preferencesManager.themeFlow.collectAsState(initial = false)

            InvestDiaryTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // --- NAVIGACE ---
                    val navController = rememberNavController()
                    val viewModel: InvestmentViewModel = viewModel() // Sdílený ViewModel

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Portfolio.route
                    ) {

                        // 1. Portfolio Screen (Domovská)
                        composable(route = Screen.Portfolio.route) {
                            PortfolioScreen(
                                viewModel = viewModel,
                                onAddClick = {
                                    navController.navigate(Screen.AddInvestment.route)
                                },
                                onSettingsClick = {
                                    navController.navigate(Screen.Settings.route)
                                },
                                onInvestmentClick = { investment ->
                                    // Navigace na detail s ID investice
                                    navController.navigate(Screen.InvestmentDetail.route + "/${investment.id}")
                                }
                            )
                        }

                        // 2. Add Investment Screen
                        composable(route = Screen.AddInvestment.route) {
                            AddInvestmentScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 3. Settings Screen
                        composable(route = Screen.Settings.route) {
                            SettingsScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 4. Investment Detail Screen (s argumentem ID)
                        composable(
                            route = Screen.InvestmentDetail.route + "/{investmentId}",
                            arguments = listOf(
                                navArgument("investmentId") {
                                    type = NavType.StringType // ID je String (UUID)
                                }
                            )
                        ) { entry ->
                            val investmentId = entry.arguments?.getString("investmentId")
                            // Najdeme investici ve ViewModelu podle ID
                            val investment = viewModel.getInvestmentById(investmentId)

                            if (investment != null) {
                                InvestmentDetailScreen(
                                    investment = investment,
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
