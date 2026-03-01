package com.example.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    // CREATE: Adds a new expense
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    // READ: Gets all expenses, sorted by newest date
    // We use "Flow" so the UI updates automatically when data changes
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    // DELETE: Removes an expense
    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    // UPDATE: Modifies an existing expense
    @Update
    suspend fun updateExpense(expense: ExpenseEntity)
}