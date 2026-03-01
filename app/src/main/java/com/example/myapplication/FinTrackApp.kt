package com.example.myapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Logic: This class is the "Power Switch" for Hilt.
 * @HiltAndroidApp tells the compiler to generate all the
 * background code needed to manage your app's memory.
 */
@HiltAndroidApp
class FinTrackApp : Application()