package com.example.shortcutpractice
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.shortcutpractice.model.App
import com.example.shortcutpractice.model.Lesson
import com.example.shortcutpractice.model.Shortcut
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import java.util.prefs.Preferences


class ShortcutViewModel : ViewModel() {
    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    val examplePreferenceFlow: Flow<String> = dataStore.data
        .map { preferences ->
            // 获取存储在 DataStore 中的值
            preferences[PreferencesKeys.EXAMPLE_KEY] ?: "default value"
        }
        .catch { exception ->
            // 处理错误情况
            if (exception is IOException) {
                emit("default value")
                exception.printStackTrace()
            } else {
                throw exception
            }
        }

    // 写入 DataStore 的方法
    suspend fun saveExamplePreference(value: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EXAMPLE_KEY] = value
        }
    }

    companion object PreferencesKeys {
        val EXAMPLE_KEY = stringPreferencesKey("example_key")
    }








    private val config = RealmConfiguration.create(schema = setOf(App::class, Lesson::class, Shortcut::class))
    private val realm: Realm = Realm.open(config)

    // LiveData 包装的 Realm 结果
    val myData: LiveData<List<App>> = liveData {
        // 使用 query API 监听数据变化
        val query = realm.query<App>()
        val results = query.find()

        // 监听变化
        val flow = query.asFlow()
        flow.collect { changes: ResultsChange<App> ->
            when (changes) {
                is InitialResults<App> -> emit(changes.list)
                is UpdatedResults<App> -> emit(changes.list)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        realm.close() // 关闭 Realm 实例
    }
}
