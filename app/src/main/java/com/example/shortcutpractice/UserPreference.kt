package com.example.shortcutpractice

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import java.util.prefs.Preferences

object UserPreference {

    // Lazy 初始化 DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    // 用于访问 DataStore 的 Context 扩展属性
    private lateinit var applicationContext: Context

    // 用于初始化单例的函数，需要在 Application 类中调用
    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    // 获取某个偏好设置项的 Flow
    val examplePreferenceFlow: Flow<String> = applicationContext.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.EXAMPLE_KEY] ?: "default value"
        }

    // 保存某个偏好设置项
    suspend fun saveExamplePreference(value: String) {
        applicationContext.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EXAMPLE_KEY] = value
        }
    }

    // 定义偏好设置键
    private object PreferencesKeys {
        val EXAMPLE_KEY = stringPreferencesKey("example_key")
    }
}