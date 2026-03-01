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

    init {
        // Check if user is already signed in
        _user.value = authRepository.getCurrentUser()
    }

    fun onSignInResult(account: GoogleSignInAccount?) {
        _user.value = account
    }

    fun signOut() {
        authRepository.signOut {
            _user.value = null
        }
    }

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

    fun addVoiceExpense(spokenText: String) {
        viewModelScope.launch {
            val amount = spokenText.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
            val newExpense = ExpenseEntity(
                title = spokenText.take(20) + "...",
                amount = amount,
                category = "Voice Entry",
                date = System.currentTimeMillis(),
                note = spokenText
            )
            repository.insertExpense(newExpense)
        }
    }

    fun addScannedExpense(scannedText: String) {
        viewModelScope.launch {
            val amount = Regex("""\d+\.\d+""").find(scannedText)?.value?.toDoubleOrNull() ?: 0.0
            val category = getSmartCategory(scannedText)
            val newExpense = ExpenseEntity(
                title = "Scanned: ${scannedText.take(15)}",
                amount = amount,
                category = category,
                date = System.currentTimeMillis()
            )
            repository.insertExpense(newExpense)
        }
    }

    private val _budgetLimit = MutableStateFlow(5000.0)
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
