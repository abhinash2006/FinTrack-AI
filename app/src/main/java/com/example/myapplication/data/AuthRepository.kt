package com.example.myapplication.data

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class AuthRepository(private val context: Context) {

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken("326288814272-j99cbjlnkkvujuke6km71oib8pntl4gg.apps.googleusercontent.com")
        .build()

    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    fun getSignInIntent() = googleSignInClient.signInIntent

    fun getCurrentUser(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>): GoogleSignInAccount? {
        return try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d("AuthRepository", "Sign-in successful: ${account?.email}")
            account
        } catch (e: ApiException) {
            // This is where you will see error code 10 (Developer Error) if SHA-1 is missing
            Log.e("AuthRepository", "Sign-in failed: status code = ${e.statusCode}, message = ${e.message}")
            null
        }
    }

    fun signOut(onComplete: () -> Unit) {
        googleSignInClient.signOut().addOnCompleteListener {
            onComplete()
        }
    }
}
