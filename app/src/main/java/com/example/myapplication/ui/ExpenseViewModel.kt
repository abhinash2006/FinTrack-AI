package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AuthRepository
import com.example.myapplication.data.ExpenseEntity
import com.example.myapplication.domain.ExpenseRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<GoogleSignInAccount?>(null)
    val user: StateFlow<GoogleSignInAccount?> = _user.asStateFlow()

    fun onSignInResult(account: GoogleSignInAccount?) {
        _user.value = account
    }

    // Logic: We convert the Database Flow into a StateFlow for the Compose UI.
    val allExpenses: StateFlow<List<ExpenseEntity>> = repository.getAllExpenses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addExpense(title: String, amount: Double, category: String) {
        viewModelScope.launch {
            val newExpense = ExpenseEntity(
                title = title,
                amount = amount,
                category = category,
                date = System.currentTimeMillis()
            )
            repository.insertExpense(newExpense)
        }
    }

    fun removeExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
    // Add this inside your ExpenseViewModel class
    fun addVoiceExpense(spokenText: String) {
        viewModelScope.launch {
            // Simple AI Logic: Find the first number in the spoken sentence
            val amount = spokenText.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0

            val newExpense = ExpenseEntity(
                title = spokenText.take(20) + "...", // Use start of sentence as title
                amount = amount,
                category = "Voice Entry",
                date = System.currentTimeMillis(),
                note = spokenText
            )
            repository.insertExpense(newExpense)
        }
    }
    // Add this inside your ExpenseViewModel class
    fun addScannedExpense(scannedText: String) {
        viewModelScope.launch {
            val amount = Regex("""\d+\.\d+""").find(scannedText)?.value?.toDoubleOrNull() ?: 0.0
            val category = getSmartCategory(scannedText) // Smart Category Logic

            val newExpense = ExpenseEntity(
                title = "Scanned: ${scannedText.take(15)}",
                amount = amount,
                category = category,
                date = System.currentTimeMillis()
            )
            repository.insertExpense(newExpense)
        }
    }
    private val _budgetLimit = MutableStateFlow(5000.0) // Default limit: Rs 5000
    val budgetLimit: StateFlow<Double> = _budgetLimit.asStateFlow()

    fun updateBudgetLimit(newLimit: Double) {
        _budgetLimit.value = newLimit
    }

    fun exportExpensesToCSV(): String {
        val header = "Title,Amount,Category,Date\n"
        val rows = allExpenses.value.joinToString("\n") {
            "${it.title},${it.amount},${it.category},${it.date}"
        }
        return header + rows
    }

    private fun getSmartCategory(text: String): String {
        val input = text.lowercase()
        return when {
            input.contains("zomato") || input.contains("food") || input.contains("restaurant") -> "Food & Dining"
            input.contains("petrol") || input.contains("fuel") || input.contains("uber") || input.contains("ola") -> "Transport"
            input.contains("amazon") || input.contains("flipkart") || input.contains("mall") -> "Shopping"
            input.contains("recharge") || input.contains("bill") || input.contains("jio") -> "Utilities"
            else -> "General"
        }
    }
}
