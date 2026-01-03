package com.example.investdiary.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.investdiary.model.Investment
import com.example.investdiary.viewmodel.InvestmentViewModel

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
    val totalValue = viewModel.getTotalValue()
    val totalProfitLoss = viewModel.getTotalProfitLoss()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moje Portfolio") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Nastavení"
                        )
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
            // Statistiky portfolia
            PortfolioStats(totalValue = totalValue, profitLoss = totalProfitLoss)

            // Seznam investic
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (investments.isEmpty()) {
                EmptyPortfolio()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(investments) { investment ->
                        InvestmentCard(
                            investment = investment,
                            onClick = { onInvestmentClick(investment) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PortfolioStats(totalValue: Double, profitLoss: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Celková hodnota",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = "${String.format("%.2f", totalValue)} Kč",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profit/Ztráta: ",
                    fontSize = 14.sp
                )
                Text(
                    text = "${if (profitLoss >= 0) "+" else ""}${String.format("%.2f", profitLoss)} Kč",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (profitLoss >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                )
            }
        }
    }
}

@Composable
fun InvestmentCard(investment: Investment, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    text = investment.ticker,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = investment.name,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "${investment.quantity} ks @ ${String.format("%.2f", investment.buyPrice)} Kč",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${String.format("%.2f", investment.currentValue)} Kč",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${if (investment.profitLoss >= 0) "+" else ""}${String.format("%.2f", investment.profitLoss)} Kč",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (investment.profitLoss >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                )
                Text(
                    text = "${if (investment.profitLossPercent >= 0) "+" else ""}${String.format("%.1f", investment.profitLossPercent)}%",
                    fontSize = 12.sp,
                    color = if (investment.profitLossPercent >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                )
            }
        }
    }
}

@Composable
fun EmptyPortfolio() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📊",
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Žádné investice",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Klikni na + pro přidání první investice",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
