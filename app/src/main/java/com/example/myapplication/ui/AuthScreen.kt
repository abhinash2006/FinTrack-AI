package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthScreen(onGoogleSignIn: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to FinTrack", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text("Manage your expenses with AI", color = Color.Gray, modifier = Modifier.padding(bottom = 32.dp))

        // Professional Email/Password Fields
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { /* Handle Email Login */ },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Login / Sign Up")
        }

        // The "Continue with Google" Divider
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 24.dp)) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text("  OR  ", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        // Professional Google Button
        OutlinedButton(
            onClick = onGoogleSignIn,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Continue with Google", fontWeight = FontWeight.Bold)
        }
    }
}