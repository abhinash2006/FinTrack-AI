package com.example.myapplication.ui

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ReceiptScanner
import com.google.android.gms.auth.api.signin.GoogleSignIn
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val expenses by viewModel.allExpenses.collectAsState()
    val user by viewModel.user.collectAsState()
    val currentLimit by viewModel.budgetLimit.collectAsState()
    val context = LocalContext.current

    var isCameraOpen by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var newLimitInput by remember { mutableStateOf(currentLimit.toString()) }

    val scanner = remember { ReceiptScanner(context) }

    // Launcher for Voice Input
    val voiceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        data?.get(0)?.let { viewModel.addVoiceExpense(it) }
    }

    // Launcher for Google Sign-In
    val authLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        viewModel.onSignInResult(viewModel.authRepository.handleSignInResult(task))
    }

    // Budget Update Dialog
    if (showBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title = { Text("Set Monthly Budget") },
            text = {
                OutlinedTextField(
                    value = newLimitInput,
                    onValueChange = { newLimitInput = it },
                    label = { Text("Amount (Rs)") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateBudgetLimit(newLimitInput.toDoubleOrNull() ?: currentLimit)
                    showBudgetDialog = false
                }) { Text("Update") }
            }
        )
    }

    if (isCameraOpen) {
        CameraPreview(onImageCaptured = { uri ->
            isCameraOpen = false
            scanner.scanReceipt(uri, { viewModel.addScannedExpense(it) }, { it.printStackTrace() })
        })
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("FINTRACK AI", fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp) },
                    actions = {
                        // Functionality 3: CSV Export Button
                        IconButton(onClick = {
                            val csvData = viewModel.exportExpensesToCSV()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, csvData)
                                type = "text/csv"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Export Expenses"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Export CSV")
                        }

                        IconButton(onClick = onThemeToggle) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme"
                            )
                        }

                        if (user == null) {
                            TextButton(onClick = {
                                authLauncher.launch(viewModel.authRepository.getSignInIntent())
                            }) {
                                Text("LOGIN", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(end = 12.dp).size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = user?.displayName?.take(1) ?: "U", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                Column(horizontalAlignment = Alignment.End) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            }
                            voiceLauncher.launch(intent)
                        },
                        icon = { Text("🎤") },
                        text = { Text("Voice") },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FloatingActionButton(onClick = { isCameraOpen = true }) {
                        Icon(Icons.Default.Add, "Scan")
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                if (expenses.isNotEmpty()) {
                    item {
                        val totalSpent = expenses.sumOf { it.amount }

                        // Functionality 2: Predictive Spending Logic
                        val calendar = Calendar.getInstance()
                        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        val predictedTotal = (totalSpent / currentDay) * daysInMonth
                        val isPredictiveWarning = predictedTotal > currentLimit

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Monthly Budget Status", style = MaterialTheme.typography.labelMedium)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Rs $totalSpent / Rs $currentLimit",
                                    color = if (totalSpent > currentLimit) Color.Red else MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = { showBudgetDialog = true }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Limit")
                                }
                            }

                            if (isPredictiveWarning && totalSpent <= currentLimit) {
                                Text(
                                    text = "⚠️ Trend suggests you will hit Rs ${predictedTotal.toInt()} by month end!",
                                    color = Color(0xFFE67E22), // Orange warning
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            LinearProgressIndicator(
                                progress = (totalSpent / currentLimit).toFloat().coerceIn(0f, 1f),
                                modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = 8.dp),
                                color = if (totalSpent > currentLimit) Color.Red else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            SpendingPieChart(expenses)
                        }
                    }
                    item {
                        Text("Recent History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))
                    }
                }

                items(expenses) { expense ->
                    ExpenseItem(expense, onDelete = { viewModel.removeExpense(expense) })
                }
            }
        }
    }
}