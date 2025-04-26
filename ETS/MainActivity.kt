package com.example.mymoneynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.mymoneynotes.ui.theme.MyMoneyNotesTheme
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Transaction(
    val category: String,
    val amount: Int,
    val date: LocalDateTime
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyMoneyNotesTheme {
                val navController = rememberNavController()
                val transactions = remember {mutableStateListOf<Transaction>().apply {
                    addAll(
                        listOf(
                            Transaction("Gaji", 5000000, LocalDateTime.now().minusSeconds(1)),
                            Transaction("Makan", -25000, LocalDateTime.now()),
                            Transaction("Transport", -15000, LocalDateTime.now().plusSeconds(1))
                        )
                    )
                }}

                NavHost(navController = navController, startDestination = "list") {
                    composable("list") {
                        Scaffold { innerPadding ->
                            TransactionListScreen(
                                transactions = transactions.sortedByDescending { it.date },
                                modifier = Modifier.padding(innerPadding),
                                navigateToAdd = { navController.navigate("add") }
                            )
                        }
                    }

                    composable("add") {
                        AddTransactionScreen(
                            onAdd = { newTransaction ->
                                transactions.add(0, newTransaction)
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionListScreen(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
    navigateToAdd: () -> Unit = {}
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToAdd) {
                Text("+")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CumulativeChart(transactions = transactions)
            LazyColumn {
                items(transactions) { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }
}

@Composable
fun CumulativeChart(transactions: List<Transaction>, modifier: Modifier = Modifier) {
    val entries = remember(transactions) {
        val sorted = transactions.sortedBy { it.date }
        val dataPoints = mutableListOf<Entry>()
        var cumulativeSum = 0f
        sorted.forEachIndexed { index, transaction ->
            cumulativeSum += transaction.amount
            dataPoints.add(Entry(index.toFloat(), cumulativeSum))
        }
        dataPoints
    }

    val axisColor = MaterialTheme.colorScheme.onBackground  // Dark mode-friendly colors.
    val lineColor = MaterialTheme.colorScheme.primary

    AndroidView(    //Not native, so using AndroidView.
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(200.dp),
        factory = { context ->
            LineChart(context).apply {
                this.data = LineData(LineDataSet(entries, "Saldo Total").apply {
                    color = lineColor.hashCode() // Adapt line color to theme
                    valueTextSize = 12f
                    setDrawValues(false)
                    setDrawCircles(true)
                })
                this.description.isEnabled = false
                this.axisRight.isEnabled = false
                this.xAxis.position = XAxis.XAxisPosition.BOTTOM
                this.xAxis.textColor = axisColor.hashCode()
                this.axisLeft.textColor = axisColor.hashCode()
                this.legend.textColor = axisColor.hashCode()
                this.invalidate()
            }
        }
    )
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val incomeColor = if (transaction.amount > 0) Color(0xFF4CAF50) else Color(0xFFF44336)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${if (transaction.amount > 0) "+" else "-"} Rp${kotlin.math.abs(transaction.amount)}",
                    color = incomeColor,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = transaction.date.toLocalDate().toString(),
                    fontSize = 16.sp
                )
            }
            Text(
                text = transaction.category,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AddTransactionScreen(onAdd: (Transaction) -> Unit) {
    var category by remember { mutableStateOf(TextFieldValue("")) }
    var amount by remember { mutableStateOf(TextFieldValue("")) }
    var date by remember { mutableStateOf(TextFieldValue(LocalDate.now().toString())) }
    var time by remember { mutableStateOf(TextFieldValue(LocalTime.now().withSecond(0).withNano(0).toString())) }

    Scaffold { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Tambah Transaksi", fontSize = 20.sp)

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Kategori") }
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Jumlah (in Rupiah)") }
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Tanggal (YYYY-MM-DD)") }
            )

            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Waktu (HH:MM; 24h)") }
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Button(onClick = {
                val amountInt = amount.text.toIntOrNull()
                val dateTime = try {
                    LocalDateTime.parse("${date.text}T${time.text}")
                } catch (e: Exception) {
                    null
                }

                if (category.text.isNotBlank() && amountInt != null && dateTime != null) {
                    onAdd(
                        Transaction(
                            category = category.text,
                            amount = amountInt,
                            date = dateTime
                        )
                    )
                }
            }) {
                Text("Tambah")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionListScreenPreview() {
    val sampleTransactions = listOf(
        Transaction("Gaji", 5000000, LocalDateTime.now().minusSeconds(1)),
        Transaction("Makan", -25000, LocalDateTime.now()),
        Transaction("Transport", -15000, LocalDateTime.now().plusSeconds(1))
    )
    MyMoneyNotesTheme {
        TransactionListScreen(
            transactions = sampleTransactions,
            navigateToAdd = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddTransactionScreenPreview() {
    MyMoneyNotesTheme {
        AddTransactionScreen(onAdd = {})
    }
}
