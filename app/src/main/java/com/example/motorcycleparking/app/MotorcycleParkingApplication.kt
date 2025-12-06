package com.example.motorcycleparking.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the Motorcycle Parking app.
 * This class serves as the application-level container for Hilt dependency injection.
 * @see HiltAndroidApp
 */
@HiltAndroidApp
class MotorcycleParkingApplication : Application()