package com.example.coursework

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys


object EncryptedPrefsUtils {
    private const val PREFS_NAME = "encrypted_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"

    private fun getSharedPreferences(context: Context) =
        EncryptedSharedPreferences.create(
            PREFS_NAME,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun saveAccessToken(context: Context, token: String) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun getAccessToken(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }
}