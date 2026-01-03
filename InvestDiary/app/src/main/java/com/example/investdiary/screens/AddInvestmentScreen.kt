package com.example.investdiary.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.investdiary.model.Investment
import com.example.investdiary.viewmodel.InvestmentViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInvestmentScreen(
    viewModel: InvestmentViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    var ticker by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var buyPrice by remember { mutableStateOf("") }
    var currentPrice by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Akcie") }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }

    val types = listOf("Akcie", "ETF", "Kryptoměny", "Komodity", "Dluhopisy")
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Přidat investici") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zpět")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ticker
            OutlinedTextField(
                value = ticker,
                onValueChange = { ticker = it.uppercase() },
                label = { Text("Ticker *") },
                placeholder = { Text("AAPL, MSFT, BTC...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Název
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Název společnosti *") },
                placeholder = { Text("Apple Inc.") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Typ investice (Dropdown)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Typ investice") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    types.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Počet kusů
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Počet kusů *") },
                placeholder = { Text("10") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Nákupní cena
            OutlinedTextField(
                value = buyPrice,
                onValueChange = { buyPrice = it },
                label = { Text("Nákupní cena (Kč) *") },
                placeholder = { Text("150.50") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Aktuální cena
            OutlinedTextField(
                value = currentPrice,
                onValueChange = { currentPrice = it },
                label = { Text("Aktuální cena (Kč) *") },
                placeholder = { Text("175.25") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Poznámky
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Poznámky") },
                placeholder = { Text("Dlouhodobá investice...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tlačítko Uložit
            Button(
                onClick = {
                    if (ticker.isNotBlank() && name.isNotBlank() &&
                        quantity.isNotBlank() && buyPrice.isNotBlank() &&
                        currentPrice.isNotBlank()) {

                        val investment = Investment(
                            ticker = ticker,
                            name = name,
                            quantity = quantity.toDoubleOrNull() ?: 0.0,
                            buyPrice = buyPrice.toDoubleOrNull() ?: 0.0,
                            currentPrice = currentPrice.toDoubleOrNull() ?: 0.0,
                            type = selectedType,
                            notes = notes
                        )

                        viewModel.addInvestment(investment)

                        // Zobrazit Snackbar
                        showSnackbar = true

                        // Vrátit se zpět po 500ms
                        kotlinx.coroutines.GlobalScope.launch {
                            kotlinx.coroutines.delay(500)
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = ticker.isNotBlank() && name.isNotBlank() &&
                        quantity.isNotBlank() && buyPrice.isNotBlank() &&
                        currentPrice.isNotBlank()
            ) {
                Text("Uložit investici", style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    // Snackbar při úspěšném uložení
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(
                message = "Investice přidána! ✅",
                duration = SnackbarDuration.Short
            )
            showSnackbar = false
        }
    }
}
