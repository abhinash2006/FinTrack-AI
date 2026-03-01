package com.example.myapplication.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ExpenseEntity

@Composable
fun SpendingPieChart(expenses: List<ExpenseEntity>) {
    val categories = expenses.groupBy { it.category }
    val totalSpend = expenses.sumOf { it.amount }

    // High-contrast professional palette
    val colors = listOf(Color(0xFF6366F1), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444))

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp).padding(16.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f // Start from the top
            categories.values.forEachIndexed { index, list ->
                val sweepAngle = (list.sumOf { it.amount }.toFloat() / totalSpend.coerceAtLeast(1.0).toFloat()) * 360f
                drawArc(
                    color = colors.getOrElse(index) { Color.LightGray },
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 30f, cap = StrokeCap.Round) // Modern Donut style
                )
                startAngle += sweepAngle
            }
        }
        // Total Spend Center Text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text("Rs ${String.format("%.0f", totalSpend)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}