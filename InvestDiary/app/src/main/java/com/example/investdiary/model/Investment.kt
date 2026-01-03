package com.example.investdiary.model

data class Investment(
    val id: String = "",
    val ticker: String = "",
    val name: String = "",
    val quantity: Double = 0.0,
    val buyPrice: Double = 0.0,
    val currentPrice: Double = 0.0,
    val type: String = "Akcie",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    // Přidej toto pole:
    val lastUpdated: Long? = null
) {
    // Pro Firestore je potřeba prázdný konstruktor,
    // který Kotlin vygeneruje díky defaultním hodnotám výše (= "", = 0.0).

    // Vypočítané vlastnosti (ty se neukládají do DB, počítají se za běhu)
    // Přidáme @Exclude anotaci, pokud by to dělalo problémy,
    // ale u computed properties (get()) to většinou není nutné.

    val totalInvested: Double
        get() = quantity * buyPrice

    val currentValue: Double
        get() = quantity * currentPrice

    val profitLoss: Double
        get() = currentValue - totalInvested

    val profitLossPercent: Double
        get() = if (totalInvested > 0) (profitLoss / totalInvested) * 100 else 0.0
}

