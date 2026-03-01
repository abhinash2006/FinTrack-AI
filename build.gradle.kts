// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // Add these two lines to register the AI and Dependency Injection engines
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
}