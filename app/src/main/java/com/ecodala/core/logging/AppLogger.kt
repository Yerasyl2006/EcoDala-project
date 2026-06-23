package com.ecodala.core.logging

import android.util.Log
import com.ecodala.BuildConfig

object AppLogger {
    private const val Tag = "EcoDala"

    fun d(message: String) {
        if (BuildConfig.ECODALA_LOGGING_ENABLED) {
            Log.d(Tag, message)
        }
    }

    fun w(message: String, throwable: Throwable? = null) {
        if (BuildConfig.ECODALA_LOGGING_ENABLED) {
            Log.w(Tag, message, throwable)
        }
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (BuildConfig.ECODALA_LOGGING_ENABLED) {
            Log.e(Tag, message, throwable)
        }
    }

    fun installCrashLogging() {
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            e("Uncaught exception on ${thread.name}", throwable)
            previousHandler?.uncaughtException(thread, throwable)
        }
    }
}
