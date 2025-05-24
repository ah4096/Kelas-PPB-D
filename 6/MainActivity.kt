package com.example.simplecalc2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simplecalc2.ui.theme.SimpleCalc2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleCalc2Theme {
                CurrencyConverterUI()
            }
        }
    }
}

val currencyRates = mapOf(
    "USD" to 1.0,
    "IDR" to 16261.30,
    "JPY" to 142.63
)

@Composable
fun CurrencyConverterUI() {
    var inputCurrency by remember { mutableStateOf("USD") }
    var outputCurrency by remember { mutableStateOf("IDR") }
    var inputAmount by remember { mutableStateOf("0") }
    var convertedAmount by remember { mutableStateOf("0") }

    fun convertCurrency(amount: String): String {
        return try {
            val input = amount.toDouble()
            val rateInUSD = currencyRates[inputCurrency] ?: 1.0
            val rateOutUSD = currencyRates[outputCurrency] ?: 1.0
            val result = input / rateInUSD * rateOutUSD
            "%.2f".format(result)
        } catch (e: Exception) {
            "Invalid input"
        }
    }

    LaunchedEffect(inputCurrency, outputCurrency, inputAmount) {
        convertedAmount = convertCurrency(inputAmount)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            // Input currency dropdown
            DropdownMenuBox(
                selectedCurrency = inputCurrency,
                onCurrencySelected = { inputCurrency = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Output currency dropdown
            DropdownMenuBox(
                selectedCurrency = outputCurrency,
                onCurrencySelected = { outputCurrency = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = inputAmount,
                onValueChange = {
                    inputAmount = it
                    convertedAmount = convertCurrency(it)
                },
                label = { Text("Input Amount") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = convertedAmount,
                onValueChange = {},
                label = { Text("Converted Amount") },
                enabled = false
            )
        }
    }
}

@Composable
fun DropdownMenuBox(
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currencies = currencyRates.keys.toList()

    Box {
        Button(onClick = { expanded = true }) {
            Text(selectedCurrency)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrencyConverterUIPreview() {
    SimpleCalc2Theme {
        CurrencyConverterUI()
    }
}
