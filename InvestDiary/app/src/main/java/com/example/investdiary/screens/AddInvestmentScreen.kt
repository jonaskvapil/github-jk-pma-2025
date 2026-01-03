package com.example.investdiary.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.investdiary.datastore.PreferencesManager
import com.example.investdiary.model.Investment
import com.example.investdiary.viewmodel.InvestmentViewModel
import kotlinx.coroutines.delay
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

    val scope = rememberCoroutineScope()
    val types = listOf("Akcie", "ETF", "Kryptoměny")
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current // Potřebujeme kontext pro Toast

    // Načtení měny z DataStore
    val preferencesManager = remember { PreferencesManager(context) }
    val currency by preferencesManager.currencyFlow.collectAsState(initial = "CZK")

    // Definice kurzů
    val exchangeRate = when (currency) {
        "USD" -> 0.044
        "EUR" -> 0.040
        "GBP" -> 0.034
        else -> 1.0
    }

    val isCrypto = selectedType == "Kryptoměny"

    // Funkce pro validaci číselného vstupu
    fun validateAndSetNumber(input: String, setter: (String) -> Unit) {
        // Povolíme prázdný string (mazání)
        if (input.isEmpty()) {
            setter(input)
            return
        }

        // Regex pro číslo (povoluje jednu tečku nebo čárku)
        // Nahradíme čárku tečkou pro kontrolu
        val normalizedInput = input.replace(',', '.')

        // Zkontrolujeme, zda je to validní číslo (nebo začátek čísla)
        val isValidNumber = normalizedInput.toDoubleOrNull() != null ||
                normalizedInput.endsWith(".") // Povolit psaní desetinné tečky

        if (isValidNumber) {
            setter(input)
        } else {
            Toast.makeText(context, "Prosím zadejte pouze číslo!", Toast.LENGTH_SHORT).show()
        }
    }

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
            // 1. TYP INVESTICE
            Text(
                text = "Typ investice",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
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
                                if (type == "Kryptoměny") name = ""
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // 2. OSTATNÍ ÚDAJE
            Text(
                text = "Detaily",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            // Ticker
            OutlinedTextField(
                value = ticker,
                onValueChange = { ticker = it.uppercase() },
                label = { Text(if (isCrypto) "Symbol (např. BTC)" else "Ticker (Symbol) *") },
                placeholder = { Text(if (isCrypto) "BTC" else "AAPL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Název
            if (!isCrypto) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Název společnosti *") },
                    placeholder = { Text("Apple Inc.") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Počet kusů (VALIDACE)
            OutlinedTextField(
                value = quantity,
                onValueChange = { input ->
                    validateAndSetNumber(input) { quantity = it }
                },
                label = { Text(if (isCrypto) "Počet mincí *" else "Počet kusů *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Nákupní cena (VALIDACE)
            OutlinedTextField(
                value = buyPrice,
                onValueChange = { input ->
                    validateAndSetNumber(input) { buyPrice = it }
                },
                label = { Text("Nákupní cena ($currency) *") },
                placeholder = { Text(if (currency == "CZK") "1000" else "45.5") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Aktuální cena (VALIDACE)
            OutlinedTextField(
                value = currentPrice,
                onValueChange = { input ->
                    validateAndSetNumber(input) { currentPrice = it }
                },
                label = { Text("Aktuální cena ($currency) *") },
                placeholder = { Text(if (currency == "CZK") "1200" else "50.0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Poznámky
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Poznámky") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tlačítko Uložit
            Button(
                onClick = {
                    val isNameValid = isCrypto || name.isNotBlank()

                    // Finální kontrola formátu čísel (ošetření teček/čárek)
                    val qty = quantity.replace(',', '.').toDoubleOrNull()
                    val buy = buyPrice.replace(',', '.').toDoubleOrNull()
                    val curr = currentPrice.replace(',', '.').toDoubleOrNull()

                    if (ticker.isNotBlank() && isNameValid &&
                        qty != null && buy != null && curr != null) {

                        // PŘEPOČET NA CZK PRO ULOŽENÍ
                        val finalBuyPriceCZK = buy / exchangeRate
                        val finalCurrentPriceCZK = curr / exchangeRate

                        val investment = Investment(
                            ticker = ticker,
                            name = if (isCrypto) ticker else name,
                            quantity = qty,
                            buyPrice = finalBuyPriceCZK,
                            currentPrice = finalCurrentPriceCZK,
                            type = selectedType,
                            notes = notes
                        )

                        viewModel.addInvestment(investment)
                        showSnackbar = true

                        scope.launch {
                            delay(500)
                            onNavigateBack()
                        }
                    } else {
                        // Záchranný Toast, kdyby něco neprošlo přes enabled button
                        Toast.makeText(context, "Zkontrolujte zadané údaje!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = ticker.isNotBlank() &&
                        (isCrypto || name.isNotBlank()) &&
                        quantity.isNotBlank() && buyPrice.isNotBlank() &&
                        currentPrice.isNotBlank()
            ) {
                Text("Uložit investici", style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar("Investice přidána! ✅")
            showSnackbar = false
        }
    }
}
