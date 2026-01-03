package com.example.investdiary.navigation


sealed class Screen(val route: String) {
    object Portfolio : Screen("portfolio_screen")
    object AddInvestment : Screen("add_investment_screen")
    object Settings : Screen("settings_screen")
    object InvestmentDetail : Screen("investment_detail_screen") // <--- Tady to musí být

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
