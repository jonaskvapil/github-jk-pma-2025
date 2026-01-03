package com.example.investdiary.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.investdiary.datastore.PreferencesManager
import com.example.investdiary.model.Investment
import com.example.investdiary.viewmodel.InvestmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentDetailScreen(
    investment: Investment,
    viewModel: InvestmentViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Načtení měny
    val preferencesManager = remember { PreferencesManager(context) }
    val currency by preferencesManager.currencyFlow.collectAsState(initial = "CZK")

    // Kurz
    val exchangeRate = when (currency) {
        "USD" -> 0.044
        "EUR" -> 0.040
        "GBP" -> 0.034
        else -> 1.0
    }

    // Přepočet hodnot pro zobrazení
    val currentValueDisplay = investment.currentValue * exchangeRate
    val buyPriceDisplay = investment.buyPrice * exchangeRate
    val currentPriceDisplay = investment.currentPrice * exchangeRate
    val totalInvestedDisplay = investment.totalInvested * exchangeRate
    val profitLossDisplay = investment.profitLoss * exchangeRate

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(investment.ticker) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zpět")
                    }
                },
                actions = {
                    // EDITACE
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Upravit")
                    }

                    // Sdílení
                    IconButton(onClick = {
                        val shareText = """
                            ${investment.ticker} - ${investment.name}
                            Počet: ${investment.quantity} ks
                            Nákupní cena: ${String.format("%.2f", buyPriceDisplay)} $currency
                            Aktuální cena: ${String.format("%.2f", currentPriceDisplay)} $currency
                            Profit/Ztráta: ${String.format("%.2f", profitLossDisplay)} $currency (${String.format("%.1f", investment.profitLossPercent)}%)
                        """.trimIndent()

                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(android.content.Intent.createChooser(sendIntent, "Sdílet investici"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Sdílet")
                    }

                    // Smazání
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Smazat", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hlavní info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (investment.profitLoss >= 0)
                        Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = investment.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = investment.type,
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Aktuální hodnota",
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${String.format("%.2f", currentValueDisplay)} $currency",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${if (investment.profitLoss >= 0) "+" else ""}${String.format("%.2f", profitLossDisplay)} $currency",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (investment.profitLoss >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${if (investment.profitLossPercent >= 0) "+" else ""}${String.format("%.1f", investment.profitLossPercent)}%)",
                            fontSize = 16.sp,
                            color = if (investment.profitLossPercent >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }

            // Detaily
            DetailCard(title = "Detaily investice") {
                DetailRow("Počet kusů", "${investment.quantity}")
                Divider()
                DetailRow("Nákupní cena", "${String.format("%.2f", buyPriceDisplay)} $currency")
                Divider()
                DetailRow("Aktuální cena", "${String.format("%.2f", currentPriceDisplay)} $currency")
                Divider()
                DetailRow("Celková investice", "${String.format("%.2f", totalInvestedDisplay)} $currency")
            }

            // Poznámky
            if (investment.notes.isNotBlank()) {
                DetailCard(title = "Poznámky") {
                    Text(
                        text = investment.notes,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Datum přidání nebo aktualizace
            DetailCard(title = "Informace") {
                val dateFormat = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())

                if (investment.lastUpdated != null) {
                    DetailRow("Aktualizováno", dateFormat.format(java.util.Date(investment.lastUpdated!!)))
                } else {
                    DetailRow("Přidáno", dateFormat.format(java.util.Date(investment.timestamp)))
                }
            }
        }
    }

    // --- DIALOG PRO SMAZÁNÍ ---
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Smazat investici?") },
            text = { Text("Opravdu chcete smazat ${investment.ticker}? Tato akce je nevratná.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteInvestment(investment.id)
                        Toast.makeText(context, "Investice smazána", Toast.LENGTH_SHORT).show()
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Smazat", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Zrušit")
                }
            }
        )
    }

    // --- DIALOG PRO EDITACI ---
    if (showEditDialog) {
        // Předvyplníme hodnoty (převedené na aktuální měnu pro editaci)
        var newQuantity by remember { mutableStateOf(investment.quantity.toString()) }
        var newCurrentPrice by remember { mutableStateOf((investment.currentPrice * exchangeRate).toString()) }
        var newBuyPrice by remember { mutableStateOf((investment.buyPrice * exchangeRate).toString()) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Upravit investici") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Zde můžete upravit parametry investice.")

                    OutlinedTextField(
                        value = newQuantity,
                        onValueChange = { newQuantity = it },
                        label = { Text("Počet kusů") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newBuyPrice,
                        onValueChange = { newBuyPrice = it },
                        label = { Text("Nákupní cena ($currency)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newCurrentPrice,
                        onValueChange = { newCurrentPrice = it },
                        label = { Text("Aktuální cena ($currency)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val qty = newQuantity.replace(',', '.').toDoubleOrNull()
                        val currPriceInput = newCurrentPrice.replace(',', '.').toDoubleOrNull()
                        val buyPriceInput = newBuyPrice.replace(',', '.').toDoubleOrNull()

                        if (qty != null && currPriceInput != null && buyPriceInput != null) {
                            // PŘEPOČET ZPĚT NA CZK (aby byla databáze konzistentní)
                            val finalCurrentPriceCZK = currPriceInput / exchangeRate
                            val finalBuyPriceCZK = buyPriceInput / exchangeRate

                            val updatedInvestment = investment.copy(
                                quantity = qty,
                                currentPrice = finalCurrentPriceCZK,
                                buyPrice = finalBuyPriceCZK,
                                lastUpdated = System.currentTimeMillis() // <--- Ukládáme čas změny
                            )

                            viewModel.updateInvestment(updatedInvestment)
                            Toast.makeText(context, "Investice aktualizována ✅", Toast.LENGTH_SHORT).show()
                            showEditDialog = false
                        } else {
                            Toast.makeText(context, "Zadejte platná čísla!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Uložit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Zrušit")
                }
            }
        )
    }
}

@Composable
fun DetailCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            content()
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
