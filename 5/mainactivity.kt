package com.example.simplecalc1

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
import com.example.simplecalc1.ui.theme.SimpleCalc1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleCalc1Theme() {
                CalculatorUI()
            }
        }
    }
}

@Composable
fun CalculatorUI() {
    var num1 by remember { mutableStateOf("0") }
    var num2 by remember { mutableStateOf("0") }
    var resultText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            TextField(value = num1, onValueChange = { num1 = it }, label = { Text("Number 1") })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = num2, onValueChange = { num2 = it }, label = { Text("Number 2") })
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = {
                    resultText = try {
                        (num1.toInt() + num2.toInt()).toString()
                    } catch (e: Exception) {
                        "Invalid input"
                    }
                }) { Text("Add") }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    resultText = try {
                        (num1.toInt() - num2.toInt()).toString()
                    } catch (e: Exception) {
                        "Invalid input"
                    }
                }) { Text("Sub") }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    resultText = try {
                        (num1.toInt() * num2.toInt()).toString()
                    } catch (e: Exception) {
                        "Invalid input"
                    }
                }) { Text("Multi") }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    resultText = try {
                        (num1.toInt() / num2.toInt()).toString()
                    } catch (e: Exception) {
                        "Invalid input"
                    }
                }) { Text("Div") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = resultText,
                onValueChange = {},
                label = { Text("Result") },
                enabled = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorUIPreview() {
    SimpleCalc1Theme() {
        CalculatorUI()
    }
}
