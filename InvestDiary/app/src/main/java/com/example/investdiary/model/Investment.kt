package com.example.investdiary.model

data class Investment(
    val id: String = "",
    val ticker: String = "",           // Např. "AAPL", "MSFT"
    val name: String = "",              // Název společnosti
    val quantity: Double = 0.0,         // Počet kusů
    val buyPrice: Double = 0.0,         // Nákupní cena za kus
    val currentPrice: Double = 0.0,     // Aktuální cena
    val type: String = "Akcie",         // Typ: Akcie, ETF, Krypto
    val notes: String = "",             // Poznámky
    val timestamp: Long = System.currentTimeMillis()  // Datum přidání
) {
    // Vypočítaná pole
    val totalInvested: Double
        get() = quantity * buyPrice

    val currentValue: Double
        get() = quantity * currentPrice

    val profitLoss: Double
        get() = currentValue - totalInvested

    val profitLossPercent: Double
        get() = if (totalInvested > 0) (profitLoss / totalInvested) * 100 else 0.0
}
