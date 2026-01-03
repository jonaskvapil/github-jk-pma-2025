package com.example.investdiary.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
                    // Sdílení
                    IconButton(onClick = {
                        val shareText = """
                            ${investment.ticker} - ${investment.name}
                            Počet: ${investment.quantity} ks
                            Nákupní cena: ${String.format("%.2f", investment.buyPrice)} Kč
                            Aktuální cena: ${String.format("%.2f", investment.currentPrice)} Kč
                            Profit/Ztráta: ${String.format("%.2f", investment.profitLoss)} Kč (${String.format("%.1f", investment.profitLossPercent)}%)
                        """.trimIndent()

                        // Intent pro sdílení
                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(android.content.Intent.createChooser(sendIntent, "Sdílet investici"))

                        // Toast
                        Toast.makeText(context, "Sdílení...", Toast.LENGTH_SHORT).show()
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
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = investment.type,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Aktuální hodnota",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${String.format("%.2f", investment.currentValue)} Kč",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${if (investment.profitLoss >= 0) "+" else ""}${String.format("%.2f", investment.profitLoss)} Kč",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (investment.profitLoss >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${if (investment.profitLossPercent >= 0) "+" else ""}${String.format("%.1f", investment.profitLossPercent)}%)",
                            fontSize = 16.sp,
                            color = if (investment.profitLossPercent >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                        )
                    }
                }
            }

            // Detaily
            DetailCard(title = "Detaily investice") {
                DetailRow("Počet kusů", "${investment.quantity}")
                HorizontalDivider()
                DetailRow("Nákupní cena", "${String.format("%.2f", investment.buyPrice)} Kč")
                HorizontalDivider()
                DetailRow("Aktuální cena", "${String.format("%.2f", investment.currentPrice)} Kč")
                HorizontalDivider()
                DetailRow("Celková investice", "${String.format("%.2f", investment.totalInvested)} Kč")
            }

            // Poznámky
            if (investment.notes.isNotBlank()) {
                DetailCard(title = "Poznámky") {
                    Text(
                        text = investment.notes,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            // Datum přidání
            DetailCard(title = "Informace") {
                DetailRow("Přidáno", java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
                    .format(java.util.Date(investment.timestamp)))
            }
        }
    }

    // Dialog pro potvrzení smazání
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
                modifier = Modifier.padding(bottom = 12.dp)
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
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
