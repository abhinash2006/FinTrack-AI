package com.example.myapplication.data

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class ReceiptScanner(private val context: Context) {
    // Logic: Initialize the ML Kit Text Recognizer
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun scanReceipt(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        try {
            val image = InputImage.fromFilePath(context, imageUri)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Logic: Extract all text found in the image
                    onSuccess(visionText.text)
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}