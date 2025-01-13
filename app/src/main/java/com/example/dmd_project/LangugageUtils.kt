package com.example.dmd_project

import android.content.Context
import android.content.SharedPreferences
import java.util.*

object LanguageUtils {
    private const val PREFS_NAME = "language_prefs"
    private const val LANGUAGE_KEY = "selected_language"

    fun setLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Save the selected language in SharedPreferences
        saveLanguage(context, language)
    }

    fun getSavedLanguage(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(LANGUAGE_KEY, "en") ?: "en" // Default to English
    }

    private fun saveLanguage(context: Context, language: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(LANGUAGE_KEY, language).apply()
    }
}