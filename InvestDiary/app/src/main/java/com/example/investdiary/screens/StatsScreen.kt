package com.example.investdiary.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.investdiary.viewmodel.InvestmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: InvestmentViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val investments by viewModel.investments.collectAsState()

    // Data pro grafy
    val totalValue = viewModel.getTotalValue()
    val typeMap = investments.groupBy { it.type }
        .mapValues { entry -> entry.value.sumOf { it.currentValue } }

    // Top Movers
    val winners = investments.filter { it.profitLoss > 0 }
        .sortedByDescending { it.profitLossPercent }
        .take(3)
    val losers = investments.filter { it.profitLoss < 0 }
        .sortedBy { it.profitLossPercent }
        .take(3)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistiky portfolia") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Koláčový graf
            if (totalValue > 0.0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Rozložení portfolia",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Vlastní PieChart komponenta
                        SimplePieChart(data = typeMap)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Legenda
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            typeMap.keys.forEach { type ->
                                val color = getColorForType(type)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier.size(12.dp),
                                        shape = MaterialTheme.shapes.small,
                                        color = color
                                    ) {}
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(type, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }

            // 2. Vítězové
            if (winners.isNotEmpty()) {
                StatsSection(title = "🏆 Nejlepší investice", items = winners, isPositive = true)
            }

            // 3. Poražení
            if (losers.isNotEmpty()) {
                StatsSection(title = "📉 Nejhorší investice", items = losers, isPositive = false)
            }

            if (totalValue == 0.0) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Zatím žádná data k analýze 🤷‍♂️")
                }
            }
        }
    }
}

@Composable
fun SimplePieChart(data: Map<String, Double>) {
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            var startAngle = -90f
            val totalValue = data.values.sum().toFloat()

            data.entries.forEach { entry ->
                val sweepAngle = (entry.value.toFloat() / totalValue) * 360f
                val color = getColorForType(entry.key)

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 40.dp.toPx())
                )
                startAngle += sweepAngle
            }
        }
        // Text uprostřed
        Text(
            text = "100%",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatsSection(title: String, items: List<com.example.investdiary.model.Investment>, isPositive: Boolean) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(item.name, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "${if (isPositive) "+" else ""}${String.format("%.1f", item.profitLossPercent)}%",
                    color = if (isPositive) Color(0xFF2E7D32) else Color(0xFFC62828),
                    fontWeight = FontWeight.Bold
                )
            }
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        }
    }
}

// Funkce pro sjednocené barvy
fun getColorForType(type: String): Color {
    return when(type) {
        "Akcie" -> Color(0xFFFFC107)      // Žlutá
        "ETF" -> Color(0xFF2196F3)        // Modrá
        "Kryptoměny" -> Color(0xFF4CAF50) // Zelená
        else -> Color(0xFFF44336)         // Červená
    }
}
