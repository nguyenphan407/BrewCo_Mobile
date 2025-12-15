package com.example.brewco.data

import android.content.Context
import android.content.SharedPreferences

class AuthManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private inline fun edit(block: SharedPreferences.Editor.() -> Unit) {
        prefs.edit().apply(block).apply()
    }

    fun saveLoginCredentials(email: String, password: String, remember: Boolean) {
        if (remember) {
            edit {
                putString(KEY_EMAIL, email)
                putString(KEY_PASSWORD, password)
                putBoolean(KEY_REMEMBER_ME, true)
            }
        } else {

            clearLoginCredentials()
        }
    }

    fun saveUserInfo(userId: String, fullName: String, phone: String) {
        edit {
            putString(KEY_USER_ID, userId)
            putString(KEY_FULL_NAME, fullName)
            putString(KEY_PHONE, phone)
        }
    }

    fun clearLoginCredentials() {

        clearRememberedCredentials()
        clearUserInfo()
    }

    fun clearRememberedCredentials() {
        edit {
            remove(KEY_EMAIL)
            remove(KEY_PASSWORD)
            putBoolean(KEY_REMEMBER_ME, false)
        }
    }

    fun clearUserInfo() {
        edit {
            remove(KEY_USER_ID)
            remove(KEY_FULL_NAME)
            remove(KEY_PHONE)
        }
    }

    fun getSavedEmail(): String= prefs.getString(KEY_EMAIL, "") ?: ""
    fun getSavedPassword(): String= prefs.getString(KEY_PASSWORD, "") ?: ""
    fun getSavedUserId(): String?= prefs.getString(KEY_USER_ID, null)
    fun getSavedFullName(): String?= prefs.getString(KEY_FULL_NAME, null)
    fun getSavedPhone(): String?= prefs.getString(KEY_PHONE, null)
    fun isRememberMeEnabled(): Boolean= prefs.getBoolean(KEY_REMEMBER_ME, false)

    fun saveAuthToken(token: String) {
        val authPrefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
        authPrefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun saveRefreshToken(token: String) {
        val authPrefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
        authPrefs.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }

    fun saveTokens(accessToken: String, refreshToken: String?) {
        val authPrefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
        authPrefs.edit()
            .putString(KEY_AUTH_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken.orEmpty())
            .apply()
    }

    fun getAuthToken(): String? {
        val authPrefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
        return authPrefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        val authPrefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
        return authPrefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun clearAuthToken() {
        val authPrefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
        authPrefs.edit().remove(KEY_AUTH_TOKEN).apply()
    }

    fun clearRefreshToken() {
        val authPrefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
        authPrefs.edit().remove(KEY_REFRESH_TOKEN).apply()
    }

    fun isLoggedIn(): Boolean {
        return !getAuthToken().isNullOrBlank()
    }

    fun logout() {
        clearAuthToken()
        clearRefreshToken()
        clearLoginCredentials()
    }

    fun clearAllData() {
        prefs.edit().clear().apply()
        val authPrefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
        authPrefs.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "brew_co_prefs"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_PHONE = "phone"


        private const val AUTH_PREFS = "auth_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"

        @Volatile
        private var instance: AuthManager? = null

        fun getInstance(context: Context): AuthManager {
            return instance ?: synchronized(this) {
                instance ?: AuthManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
