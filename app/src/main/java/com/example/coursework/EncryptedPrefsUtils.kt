package com.example.coursework

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


object EncryptedPrefsUtils {
    private const val PREFS_NAME = "encrypted_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_LAST_SYNC_TIME = "last_sync_time"

    @Volatile
    private var sharedPreferences: SharedPreferences? = null

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return sharedPreferences ?: synchronized(this) {
            sharedPreferences ?: buildSharedPreferences(context).also {
                sharedPreferences = it
            }
        }
    }

    private fun buildSharedPreferences(context: Context) : SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveAccessToken(context: Context, token: String) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun getAccessToken(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun saveLastSyncTime(context: Context, lastSyncTime: String) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit().putString(KEY_LAST_SYNC_TIME, lastSyncTime).apply()
    }

    fun getLastSyncTime(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(KEY_LAST_SYNC_TIME, null)
    }

    fun saveCycleLength(context: Context, cycleLength: Int) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit().putInt("cycle_length", cycleLength).apply()
    }

    fun getCycleLength(context: Context): Int {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getInt("cycle_length", 0)
    }

    fun savePeriodLength(context: Context, periodLength: Int) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit().putInt("period_length", periodLength).apply()
    }

    fun getPeriodLength(context: Context): Int {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getInt("period_length", 0)
    }

    fun saveLutealPhaseLength(context: Context, lutealPhaseLength: Int) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit().putInt("luteal_phase_length", lutealPhaseLength).apply()
    }

    fun getLutealPhaseLength(context: Context): Int {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getInt("luteal_phase_length", 0)
    }

}