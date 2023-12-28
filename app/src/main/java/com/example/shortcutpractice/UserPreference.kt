package com.example.shortcutpractice

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object UserPreference {
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        println("hello ")
        applicationContext = context.applicationContext
    }

    suspend fun getKeyboardTypePreference(): KeyboardType {
        val preferences = applicationContext.dataStore.data.first()
        val osName = preferences[PreferencesKeys.OS_KEY] ?: KeyboardType.WIN.name
        return KeyboardType.valueOf(osName)
    }

    suspend fun saveKeyboardTypePreference(value: KeyboardType) {
        applicationContext.dataStore.edit { mutablePreferences ->
            mutablePreferences[PreferencesKeys.OS_KEY] = value.name
        }
    }

    suspend fun getModePreference(): Mode {
        val preferences = applicationContext.dataStore.data.first()
        val modeName = preferences[PreferencesKeys.MODE_KEY] ?: Mode.LEARN.name
        return Mode.valueOf(modeName)
    }

    suspend fun saveModePreference(value: Mode) {
        applicationContext.dataStore.edit { mutablePreferences ->
            mutablePreferences[PreferencesKeys.MODE_KEY] = value.name
        }
    }

    suspend fun getCurrentShortcutIdPreference(): Int? {
        val preferences = applicationContext.dataStore.data.first()
        return preferences[PreferencesKeys.SHORTCUT_KEY]
    }

    suspend fun saveCurrentShortcutIdPreference(value: Int) {
        applicationContext.dataStore.edit { mutablePreferences ->
            mutablePreferences[PreferencesKeys.SHORTCUT_KEY] = value
        }
    }

    private object PreferencesKeys {
        val OS_KEY = stringPreferencesKey("OS_TYPE")
        val MODE_KEY = stringPreferencesKey("MODE_KEY")
        val SHORTCUT_KEY = intPreferencesKey("SHORTCUT_ID")
    }
}