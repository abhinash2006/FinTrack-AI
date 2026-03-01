package com.example.myapplication.domain

import com.example.myapplication.data.ExpenseDao
import com.example.myapplication.data.ExpenseEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    // Logic: The repository exposes the data from the DAO to the rest of the app.
    // It keeps your business logic separate from your database code.

    fun getAllExpenses(): Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    suspend fun insertExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }

    // Use a lazy delegate to ensure Firestore is only initialized when needed,
    // which gives more time for FirebaseApp to be initialized by the plugin.
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    suspend fun syncToCloud(expense: ExpenseEntity, userId: String) {
        val data = hashMapOf(
            "title" to expense.title,
            "amount" to expense.amount,
            "category" to expense.category,
            "date" to expense.date
        )

        // This creates a folder for the user and stores the expense inside it
        firestore.collection("users").document(userId)
            .collection("expenses").add(data)
    }
}
