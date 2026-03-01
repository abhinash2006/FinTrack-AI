package com.example.myapplication.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.ExpenseEntity

@Composable
fun SummaryReceipt(expenses: List<ExpenseEntity>) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        color = Color(0xFFF8F9FA), // Off-white paper color
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("FINTRACK OFFICIAL RECEIPT", fontWeight = FontWeight.ExtraBold, modifier = Modifier.align(Alignment.CenterHorizontally))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Logic: List every single input (Voice, Scan, Manual)
            expenses.forEach { expense ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(expense.title, modifier = Modifier.weight(1f))
                    Text("Rs ${expense.amount}", fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("TOTAL", fontWeight = FontWeight.Bold)
                Text("Rs ${expenses.sumOf { it.amount }}", fontWeight = FontWeight.ExtraBold, color = Color.Black)
            }
        }
    }
}
