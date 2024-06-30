package com.zeal.loyaltyapplication

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.zeal.loyaltyapplication.utils.Constants.LOYALTY_SHARED_PREFERENCES_FILE_NAME
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @created 29/06/2024 - 1:02 AM
 * @project Loyalty Application
 * @author adell
 */
class LoyaltyApp : Application() {

    private lateinit var handler: Handler
    private lateinit var backgroundExecutor: ExecutorService

    override fun onCreate() {
        super.onCreate()

        instance = this
        handler = Handler(Looper.getMainLooper())
        backgroundExecutor =
            Executors.newFixedThreadPool(10) { runnable -> // create a new thread pool
                val thread = Thread(runnable, "Background executor service")
                thread.priority = Thread.MIN_PRIORITY
                thread.isDaemon =
                    true // to let the JVM terminate the app even if the thread is running
                thread
            }
    }

    fun runOnUiThread(runnable: Runnable) {
        handler.post(runnable)
    }

    fun runOnUiThreadWithDelay(runnable: Runnable, delayMillis: Long) {
        handler.postDelayed(runnable, delayMillis)
    }

    fun runOnBackgroundThread(runnable: Runnable) {
        backgroundExecutor.execute(runnable)
    }

    fun runOnBackgroundThreadWithCallback(runnable: Runnable, callback: Runnable) {
        backgroundExecutor.execute {
            runnable.run()
            runOnUiThread(callback)
        } // run the runnable on a background thread and then run the callback on the main thread
    }

    companion object {
        private lateinit var instance: LoyaltyApp // application instance

        @Volatile
        private var sharedPreferencesInstance: SharedPreferences? =
            null

        fun getInstance(): LoyaltyApp {
            return instance
        }

        fun getSharedPreferencesInstance(): SharedPreferences {
            if (sharedPreferencesInstance == null) {
                synchronized(LoyaltyApp::class.java) {
                    if (sharedPreferencesInstance == null) {
                        sharedPreferencesInstance = instance.getSharedPreferences(
                            LOYALTY_SHARED_PREFERENCES_FILE_NAME,
                            Context.MODE_PRIVATE
                        )
                    }
                }
            }
            return sharedPreferencesInstance as SharedPreferences
        }
    }
}