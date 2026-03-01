package com.example.myapplication.data

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class AuthRepository(private val context: Context) {

    // Logic: This creates the settings for Google Sign-In
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        // PASTE YOUR WEB CLIENT ID BELOW
        .requestIdToken("1040213731683-8qtlaqveuf1hi72thfrbqcf4ebt6ku7o.apps.googleusercontent.com")
        .build()

    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    fun getSignInIntent() = googleSignInClient.signInIntent

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>): GoogleSignInAccount? {
        return try {
            completedTask.getResult(ApiException::class.java)
        } catch (e: ApiException) {
            null
        }
    }
}