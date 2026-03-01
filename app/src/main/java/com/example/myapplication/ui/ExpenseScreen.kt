package com.example.myapplication.ui

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
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
    // --- UI State Management ---
    val expenses by viewModel.allExpenses.collectAsState()
    val user by viewModel.user.collectAsState()
    val currentLimit by viewModel.budgetLimit.collectAsState()
    val context = LocalContext.current

    var isCameraOpen by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var newLimitInput by remember { mutableStateOf(currentLimit.toString()) }

    // Manual Entry States (Correctly placed inside Composable)
    var showManualDialog by remember { mutableStateOf(false) }
    var manualTitle by remember { mutableStateOf("") }
    var manualAmount by remember { mutableStateOf("") }
    var manualCategory by remember { mutableStateOf("General") }

    val scanner = remember { ReceiptScanner(context) }

    // --- Launchers ---
    val voiceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        data?.get(0)?.let { viewModel.addVoiceExpense(it) }
    }

    val authLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        viewModel.onSignInResult(viewModel.authRepository.handleSignInResult(task))
    }

    // --- Background Wrapper ---
    Box(modifier = Modifier.fillMaxSize()) {
        // Gradient Background for a professional feel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        if (isCameraOpen) {
            CameraPreview(onImageCaptured = { uri ->
                isCameraOpen = false
                scanner.scanReceipt(uri, { viewModel.addScannedExpense(it) }, { it.printStackTrace() })
            })
        } else {
            Scaffold(
                containerColor = Color.Transparent, // Transparent to show the background
                topBar = {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        ),
                        title = { Text("FINTRACK AI", fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp) },
                        actions = {
                            IconButton(onClick = {
                                val csvData = viewModel.exportExpensesToCSV()
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, csvData)
                                    type = "text/csv"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Export Expenses"))
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "Export")
                            }

                            IconButton(onClick = onThemeToggle) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Theme"
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
                        FloatingActionButton(
                            onClick = { showManualDialog = true },
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Manual")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        ExtendedFloatingActionButton(
                            onClick = {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                }
                                voiceLauncher.launch(intent)
                            },
                            icon = { Icon(Icons.Default.Mic, null) },
                            text = { Text("Voice") },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LargeFloatingActionButton(
                            onClick = { isCameraOpen = true },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(Icons.Default.PhotoCamera, "Scan", modifier = Modifier.size(36.dp))
                        }
                    }
                }
            ) { paddingValues ->
                // --- Manual Entry Dialog ---
                if (showManualDialog) {
                    AlertDialog(
                        onDismissRequest = { showManualDialog = false },
                        title = { Text("Add Expense", fontWeight = FontWeight.Bold) },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = manualTitle,
                                    onValueChange = { manualTitle = it },
                                    label = { Text("Title") },
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                )
                                OutlinedTextField(
                                    value = manualAmount,
                                    onValueChange = { manualAmount = it },
                                    label = { Text("Amount (Rs)") },
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                )
                                Text("Category", style = MaterialTheme.typography.labelSmall)
                                val cats = listOf("Food", "Travel", "Bill", "Shop")
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    cats.forEach { cat ->
                                        FilterChip(
                                            selected = manualCategory == cat,
                                            onClick = { manualCategory = cat },
                                            label = { Text(cat) }
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                val amt = manualAmount.toDoubleOrNull() ?: 0.0
                                if (manualTitle.isNotEmpty() && amt > 0.0) {
                                    viewModel.addManualExpense(manualTitle, amt, manualCategory)
                                    showManualDialog = false
                                    manualTitle = ""
                                    manualAmount = ""
                                }
                            }) { Text("Add") }
                        }
                    )
                }

                // --- Main Content ---
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    if (expenses.isNotEmpty()) {
                        item {
                            BudgetStatusHeader(expenses, currentLimit) { showBudgetDialog = true }
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                            ) {
                                Box(Modifier.padding(16.dp).fillMaxWidth(), Alignment.Center) {
                                    SpendingPieChart(expenses)
                                }
                            }
                        }
                    }
                    items(expenses) { expense ->
                        ExpenseItem(expense, onDelete = { viewModel.removeExpense(expense) })
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetStatusHeader(expenses: List<com.example.myapplication.data.ExpenseEntity>, limit: Double, onEdit: () -> Unit) {
    val totalSpent = expenses.sumOf { it.amount }
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Monthly Budget", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onEdit) { Icon(Icons.Default.Settings, null) }
            }
            Text("Rs ${totalSpent.toInt()} / Rs ${limit.toInt()}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            LinearProgressIndicator(
                progress = (totalSpent / limit).toFloat().coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = 8.dp),
                color = if (totalSpent > limit) Color.Red else MaterialTheme.colorScheme.primary
            )
        }
    }
}