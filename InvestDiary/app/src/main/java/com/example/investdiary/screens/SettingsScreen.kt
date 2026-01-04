package com.example.investdiary.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.investdiary.datastore.PreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val scope = rememberCoroutineScope()

    // Načtení hodnot
    val currency by preferencesManager.currencyFlow.collectAsState(initial = "CZK")
    val isDarkTheme by preferencesManager.themeFlow.collectAsState(initial = false)
    val notificationsEnabled by preferencesManager.notificationsFlow.collectAsState(initial = true)

    // Načítáme základní (uloženou) částku v CZK
    val targetAmountBase by preferencesManager.targetAmountFlow.collectAsState(initial = 100000.0)

    val portfolioName by preferencesManager.portfolioNameFlow.collectAsState(initial = "Moje Portfolio")

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showTargetDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }

    // --- PŘEPOČET KURZU (Stejná logika jako v PortfolioScreen) ---
    val exchangeRate = when (currency) {
        "USD" -> 0.044
        "EUR" -> 0.040
        "GBP" -> 0.034
        else -> 1.0
    }

    // Částka pro zobrazení v UI (přepočtená)
    val targetAmountDisplay = targetAmountBase * exchangeRate

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nastavení") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zpět")
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // --- SEKCE: OSOBNÍ ---
            Text(
                text = "Osobní",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Název portfolia
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showNameDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Název portfolia",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Zobrazuje se na hlavní obrazovce",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = portfolioName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Upravit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- SEKCE: ZOBRAZENÍ ---
            Text(
                text = "Zobrazení",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Měna
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCurrencyDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Měna", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Vyberte měnu zobrazení", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(text = currency, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                }
            }

            // Tmavý režim
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Tmavý režim", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Přepnout vzhled aplikace", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isChecked -> scope.launch { preferencesManager.setTheme(isChecked) } }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- SEKCE: CÍLE ---
            Text(
                text = "Cíle",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().clickable { showTargetDialog = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Finanční cíl", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Cílová částka portfolia ($currency)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    // ZDE JE ZMĚNA: Používáme targetAmountDisplay (přepočtené)
                    Text(text = "${String.format("%.0f", targetAmountDisplay)}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- SEKCE: NOTIFIKACE ---
            Text(text = "Notifikace", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Povolit notifikace", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Upozornění aplikace", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = notificationsEnabled, onCheckedChange = { isChecked -> scope.launch { preferencesManager.setNotifications(isChecked) } })
                }
            }
        }
    }

    // --- DIALOGY ---

    // Dialog pro jméno
    if (showNameDialog) {
        var tempName by remember { mutableStateOf(portfolioName) }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Název portfolia") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Název") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (tempName.isNotBlank()) {
                            scope.launch { preferencesManager.setPortfolioName(tempName) }
                            showNameDialog = false
                        }
                    }
                ) { Text("Uložit") }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) { Text("Zrušit") }
            }
        )
    }

    // Dialog pro měnu
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Vyberte měnu") },
            text = {
                Column {
                    listOf("CZK", "USD", "EUR", "GBP").forEach { currencyOption ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch { preferencesManager.setCurrency(currencyOption) }
                                    showCurrencyDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currency == currencyOption,
                                onClick = {
                                    scope.launch { preferencesManager.setCurrency(currencyOption) }
                                    showCurrencyDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = currencyOption)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showCurrencyDialog = false }) { Text("Zavřít") } }
        )
    }

    // Dialog pro Cíl - ZDE POZOR: Musíme uživatele nechat zadat částku v JEHO měně, ale uložit v CZK
    if (showTargetDialog) {
        // Předvyplníme aktuální zobrazenou hodnotou (např. 4400 USD)
        var tempAmount by remember { mutableStateOf(targetAmountDisplay.toString()) }

        AlertDialog(
            onDismissRequest = { showTargetDialog = false },
            title = { Text("Nastavit finanční cíl ($currency)") },
            text = {
                OutlinedTextField(
                    value = tempAmount,
                    onValueChange = { tempAmount = it },
                    label = { Text("Částka v $currency") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val inputAmount = tempAmount.toDoubleOrNull()
                    if (inputAmount != null && inputAmount > 0) {
                        // Přepočet zpět na CZK pro uložení
                        val amountInCZK = inputAmount / exchangeRate
                        scope.launch { preferencesManager.setTargetAmount(amountInCZK) }
                        showTargetDialog = false
                    }
                }) { Text("Uložit") }
            },
            dismissButton = { TextButton(onClick = { showTargetDialog = false }) { Text("Zrušit") } }
        )
    }
}
