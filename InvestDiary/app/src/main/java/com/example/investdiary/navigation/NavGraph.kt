package com.example.investdiary.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.investdiary.model.Investment
import com.example.investdiary.screens.AddInvestmentScreen
import com.example.investdiary.screens.InvestmentDetailScreen
import com.example.investdiary.screens.PortfolioScreen
import com.example.investdiary.screens.SettingsScreen
import com.example.investdiary.viewmodel.InvestmentViewModel

sealed class Screen(val route: String) {
    object Portfolio : Screen("portfolio")
    object AddInvestment : Screen("add_investment")
    object Detail : Screen("detail")
    object Settings : Screen("settings")  // Nová route
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: InvestmentViewModel
) {
    var selectedInvestment: Investment? = null

    NavHost(
        navController = navController,
        startDestination = Screen.Portfolio.route
    ) {
        composable(Screen.Portfolio.route) {
            PortfolioScreen(
                viewModel = viewModel,
                onAddClick = {
                    navController.navigate(Screen.AddInvestment.route)
                },
                onInvestmentClick = { investment ->
                    selectedInvestment = investment
                    navController.navigate(Screen.Detail.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.AddInvestment.route) {
            AddInvestmentScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Detail.route) {
            selectedInvestment?.let { investment ->
                InvestmentDetailScreen(
                    investment = investment,
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Nová route pro Settings
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
