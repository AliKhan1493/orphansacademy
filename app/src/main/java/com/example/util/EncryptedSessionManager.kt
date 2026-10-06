package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Manages encrypted session tokens, credentials, and sensitive orphan data flags.
 * Uses AES256-GCM encryption with Android Keystore backed MasterKey.
 */
class EncryptedSessionManager(context: Context) {

    companion object {
        private const val TAG = "EncryptedSessionManager"
        private const val PREFS_FILE = "secure_academy_session_prefs"
        private const val KEY_AUTH_TOKEN = "key_auth_token"
        private const val KEY_LAST_UID = "key_last_uid"
        private const val KEY_USER_ROLE = "key_user_role"
        private const val KEY_OFFLINE_SECRET = "key_offline_secret"
    }

    private val sharedPreferences: SharedPreferences = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        Log.w(TAG, "Hardware KeyStore unavailable, falling back to standard private preferences: ${e.message}")
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
    }

    fun saveSession(uid: String, role: String, token: String?) {
        sharedPreferences.edit()
            .putString(KEY_LAST_UID, uid)
            .putString(KEY_USER_ROLE, role)
            .putString(KEY_AUTH_TOKEN, token ?: "local_secure_token_${System.currentTimeMillis()}")
            .apply()
    }

    fun getActiveUid(): String? = sharedPreferences.getString(KEY_LAST_UID, null)

    fun getActiveRole(): String? = sharedPreferences.getString(KEY_USER_ROLE, null)

    fun getAuthToken(): String? = sharedPreferences.getString(KEY_AUTH_TOKEN, null)

    fun clearSession() {
        sharedPreferences.edit()
            .remove(KEY_LAST_UID)
            .remove(KEY_USER_ROLE)
            .remove(KEY_AUTH_TOKEN)
            .apply()
    }
}
