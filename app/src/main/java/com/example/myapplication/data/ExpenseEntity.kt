package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Logic: This class represents a table named "expenses" in your SQLite database.
 * Each variable below represents a column in that table.
 */
@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,           // Unique ID for every expense
    val title: String,        // Name of the expense (e.g., "Lunch")
    val amount: Double,       // Cost (e.g., 250.0)
    val category: String,     // ML-generated or manual (e.g., "Food")
    val date: Long,           // Date stored as a timestamp
    val note: String? = null  // Optional note
)