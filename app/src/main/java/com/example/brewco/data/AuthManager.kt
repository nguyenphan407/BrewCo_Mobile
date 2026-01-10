package com.example.brewco.data

import android.content.Context
import android.content.SharedPreferences

class AuthManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveLoginCredentials(email: String, password: String, remember: Boolean) {
        if (remember) {
            prefs.edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .putBoolean(KEY_REMEMBER_ME, true)
                .apply()
        } else {
            // Xóa thông tin đăng nhập nếu không chọn ghi nhớ
            clearLoginCredentials()
        }
    }

    fun saveUserInfo(userId: String, fullName: String, phone: String) {
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_FULL_NAME, fullName)
            .putString(KEY_PHONE, phone)
            .apply()
    }

    fun clearLoginCredentials() {
        prefs.edit()
            .remove(KEY_EMAIL)
            .remove(KEY_PASSWORD)
            .remove(KEY_USER_ID)
            .remove(KEY_FULL_NAME)
            .remove(KEY_PHONE)
            .putBoolean(KEY_REMEMBER_ME, false)
            .apply()
    }

    fun getSavedEmail(): String {
        return prefs.getString(KEY_EMAIL, "") ?: ""
    }

    fun getSavedPassword(): String {
        return prefs.getString(KEY_PASSWORD, "") ?: ""
    }

    fun getSavedUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun getSavedFullName(): String? {
        return prefs.getString(KEY_FULL_NAME, null)
    }

    fun getSavedPhone(): String? {
        return prefs.getString(KEY_PHONE, null)
    }

    fun isRememberMeEnabled(): Boolean {
        return prefs.getBoolean(KEY_REMEMBER_ME, false)
    }

    companion object {
        private const val PREF_NAME = "brew_co_prefs"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_PHONE = "phone"

        @Volatile
        private var instance: AuthManager? = null

        fun getInstance(context: Context): AuthManager {
            return instance ?: synchronized(this) {
                instance ?: AuthManager(context.applicationContext).also { instance = it }
            }
        }
    }
}