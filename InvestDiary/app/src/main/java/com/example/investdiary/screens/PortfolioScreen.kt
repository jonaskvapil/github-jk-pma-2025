package com.example.investdiary.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: InvestmentViewModel = viewModel(),
    onAddClick: () -> Unit = {},
    onInvestmentClick: (Investment) -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val investments by viewModel.investments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    // OVLÁDÁNÍ DRAWERU (BOČNÍHO MENU)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Načtení dat z ViewModel
    val totalValueCZK = viewModel.getTotalValue()
    val totalProfitLossCZK = viewModel.getTotalProfitLoss()

    // Načtení nastavení
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val targetAmountCZK by preferencesManager.targetAmountFlow.collectAsState(initial = 100000.0)
    val currency by preferencesManager.currencyFlow.collectAsState(initial = "CZK")
    val portfolioName by preferencesManager.portfolioNameFlow.collectAsState(initial = "Moje Portfolio")

    // Stav pro dialog na změnu cíle
    var showUpdateGoalDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val exchangeRate = when (currency) {
        "USD" -> 0.044
        "EUR" -> 0.040
        "GBP" -> 0.034
        else -> 1.0
    }

    val totalValueDisplay = totalValueCZK * exchangeRate
    val totalProfitLossDisplay = totalProfitLossCZK * exchangeRate
    val targetAmountDisplay = targetAmountCZK * exchangeRate

    val rawProgress = if (targetAmountCZK > 0) (totalValueCZK / targetAmountCZK).toFloat() else 0f
    val progress = rawProgress.coerceIn(0f, 1f)
    val progressPercent = (rawProgress * 100)
    val isGoalReached = rawProgress >= 1.0f

    // --- NAVIGATION DRAWER ---
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "InvestDiary",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Divider()

                // Položka: Portfolio
                NavigationDrawerItem(
                    label = { Text(text = "Portfolio") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // Položka: Nastavení
                NavigationDrawerItem(
                    label = { Text(text = "Nastavení") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onSettingsClick()
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // Položka: O aplikaci
                NavigationDrawerItem(
                    label = { Text(text = "O aplikaci") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showAboutDialog = true
                    },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        // --- HLAVNÍ OBSAH (SCAFFOLD) ---
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(portfolioName) },
                    // Ikona Hamburger Menu
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAddClick) {
                    Icon(Icons.Default.Add, contentDescription = "Přidat investici")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Statistiky
                    item {
                        PortfolioStats(
                            totalValue = totalValueDisplay,
                            profitLoss = totalProfitLossDisplay,
                            currencySymbol = currency
                        )
                    }

                    // 2. Cíle
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isGoalReached) Color(0xFFFFF8E1) else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isGoalReached) "Cíl splněn! 🎉" else "Cíl: ${String.format("%.0f", targetAmountDisplay)} $currency",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${String.format("%.1f", progressPercent)}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isGoalReached) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = progress,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (isGoalReached) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )

                                if (isGoalReached) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { showUpdateGoalDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text("Navýšit cíl 🚀")
                                    }
                                }
                            }
                        }
                    }

                    // 3. Diverzifikace
                    if (investments.isNotEmpty()) {
                        item { DiversificationBar(investments = investments) }
                    }

                    // 4. Seznam
                    if (isLoading) {
                        item { Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                    } else if (investments.isEmpty()) {
                        item { EmptyPortfolio() }
                    } else {
                        items(investments) { investment ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                InvestmentCard(
                                    investment = investment,
                                    exchangeRate = exchangeRate,
                                    currencySymbol = currency,
                                    onClick = { onInvestmentClick(investment) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGY ---
    if (showUpdateGoalDialog) {
        var tempAmount by remember { mutableStateOf((targetAmountCZK * 1.5).toString()) }
        AlertDialog(
            onDismissRequest = { showUpdateGoalDialog = false },
            title = { Text("Gratulujeme! 🎉") },
            text = {
                Column {
                    Text("Dosáhli jste svého cíle. Chcete si nastavit nový?")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = tempAmount,
                        onValueChange = { tempAmount = it },
                        label = { Text("Nový cíl (CZK)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val newAmount = tempAmount.toDoubleOrNull()
                    if (newAmount != null && newAmount > targetAmountCZK) {
                        scope.launch { preferencesManager.setTargetAmount(newAmount) }
                        showUpdateGoalDialog = false
                    }
                }) { Text("Uložit nový cíl") }
            },
            dismissButton = { TextButton(onClick = { showUpdateGoalDialog = false }) { Text("Zrušit") } }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("O aplikaci") },
            text = { Text("InvestDiary v1.0\nCreated by Jony\n\nSemestrální práce 2026") },
            confirmButton = { TextButton(onClick = { showAboutDialog = false }) { Text("OK") } }
        )
    }
}

// Zbytek funkcí zůstává stejný (PortfolioStats, DiversificationBar, InvestmentCard, EmptyPortfolio)
@Composable
fun PortfolioStats(totalValue: Double, profitLoss: Double, currencySymbol: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Celková hodnota", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(text = "${String.format("%.2f", totalValue)} $currencySymbol", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Profit/Ztráta: ", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text(text = "${if (profitLoss >= 0) "+" else ""}${String.format("%.2f", profitLoss)} $currencySymbol", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (profitLoss >= 0) Color(0xFF2E7D32) else Color(0xFFC62828))
            }
        }
    }
}

@Composable
fun DiversificationBar(investments: List<Investment>) {
    val dataByType = investments.groupBy { it.type }.mapValues { entry -> entry.value.sumOf { it.currentValue } }
    val totalValue = dataByType.values.sum()
    if (totalValue == 0.0) return
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Rozložení portfolia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp), color = MaterialTheme.colorScheme.onSurface)
            Row(modifier = Modifier.fillMaxWidth().height(20.dp).clip(RoundedCornerShape(4.dp))) {
                dataByType.entries.sortedByDescending { it.value }.forEachIndexed { index, entry ->
                    val weight = (entry.value / totalValue).toFloat()
                    val color = when(index % 5) { 0 -> Color(0xFF4CAF50); 1 -> Color(0xFF2196F3); 2 -> Color(0xFFFFC107); 3 -> Color(0xFF9C27B0); else -> Color(0xFFF44336) }
                    Box(modifier = Modifier.weight(weight).fillMaxHeight().background(color))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column {
                dataByType.entries.sortedByDescending { it.value }.forEachIndexed { index, entry ->
                    val percentage = (entry.value / totalValue * 100)
                    val color = when(index % 5) { 0 -> Color(0xFF4CAF50); 1 -> Color(0xFF2196F3); 2 -> Color(0xFFFFC107); 3 -> Color(0xFF9C27B0); else -> Color(0xFFF44336) }
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = entry.key, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "${String.format("%.1f", percentage)}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun InvestmentCard(investment: Investment, exchangeRate: Double, currencySymbol: String, onClick: () -> Unit) {
    val currentValueDisplay = investment.currentValue * exchangeRate
    val profitLossDisplay = investment.profitLoss * exchangeRate
    val buyPriceDisplay = investment.buyPrice * exchangeRate
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = investment.ticker, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = investment.name, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "${investment.quantity} ks @ ${String.format("%.2f", buyPriceDisplay)} $currencySymbol", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${String.format("%.2f", currentValueDisplay)} $currencySymbol", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "${if (investment.profitLoss >= 0) "+" else ""}${String.format("%.2f", profitLossDisplay)} $currencySymbol", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (investment.profitLoss >= 0) Color(0xFF2E7D32) else Color(0xFFC62828))
                Text(text = "${if (investment.profitLossPercent >= 0) "+" else ""}${String.format("%.1f", investment.profitLossPercent)}%", fontSize = 12.sp, color = if (investment.profitLossPercent >= 0) Color(0xFF2E7D32) else Color(0xFFC62828))
            }
        }
    }
}

@Composable
fun EmptyPortfolio() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "📊", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Žádné investice", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "Klikni na + pro přidání první investice", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
