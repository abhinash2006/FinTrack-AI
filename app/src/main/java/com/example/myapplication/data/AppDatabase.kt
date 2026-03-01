package com.example.myapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase

// Logic: We define which entities (tables) belong to this database and its version.
@Database(entities = [ExpenseEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // This tells Room to provide the implementation for the Dao interface we created.
    abstract fun expenseDao(): ExpenseDao
}