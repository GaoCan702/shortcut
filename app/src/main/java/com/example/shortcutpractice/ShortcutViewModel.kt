package com.example.shortcutpractice


import ShortcutDao
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shortcutpractice.model.Shortcut
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch


class ShortcutViewModel : ViewModel() {

    val userInput: MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf())

    val currentKeyboardType: MutableLiveData<KeyboardType> = MutableLiveData()
    val currentMode: MutableLiveData<Mode> = MutableLiveData(Mode.LEARN)
    val searchStr = MutableStateFlow("")
    val currentShortcut: MutableStateFlow<Shortcut?> = MutableStateFlow(null)
    val currentRecommendAppList = MutableLiveData<List<String>>()
    val shortcutHistory = MutableLiveData<List<Shortcut>>(mutableListOf())


    init {
        viewModelScope.launch {
            UserPreference.getKeyboardTypePreference().let { osType ->
                currentKeyboardType.postValue(osType)
            }
        }

        viewModelScope.launch {
            UserPreference.getModePreference().let { mode ->
                currentMode.postValue(mode)
            }
        }

        viewModelScope.launch {
            val previousCurrentShortcutId: Int? = UserPreference.getCurrentShortcutIdPreference()
            if (previousCurrentShortcutId == null) {
                initCurrentShortcut()
                return@launch
            }
            ShortcutDao.queryShortcut(previousCurrentShortcutId).collect() { change ->
                when (change) {
                    is InitialObject<Shortcut>, is UpdatedObject<Shortcut> -> if (change.obj != null) changeCurrentShortcut(
                        change.obj!!
                    )
                    else -> {
                        initCurrentShortcut()
                    }
                }
            }

        }

        viewModelScope.launch {
            searchStr.collect { str ->
                ShortcutDao.queryApps(str).collect { appNames ->
                    currentRecommendAppList.postValue(appNames)
                }
            }
        }
    }

    private fun initCurrentShortcut() {
        viewModelScope.launch {
            ShortcutDao.queryFirstShortcut().collect() { change ->
                when (change) {
                    is InitialObject<Shortcut>, is UpdatedObject<Shortcut> -> change.obj?.let {
                        changeCurrentShortcut(it)
                    }
                    else -> {}
                }
            }
        }
    }

    //根据currentShortcut里的appName来查询所有的groupName,并在currentShortcut改变时候更新
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getGroupNameInAppByCurrentShortcut(): Flow<List<String>> {
        return currentShortcut.distinctUntilChangedBy { it?.appName }.flatMapLatest { shortcut ->
            if (shortcut == null) {
                flowOf(emptyList())
            } else {
                ShortcutDao.queryGroupNameByAppName(shortcut.appName)
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getShortcutsInAppByCurrentShortcut(): Flow<List<Shortcut>> {
        return currentShortcut.distinctUntilChangedBy { it?.appName } // This ensures that only changes in appName will emit downstream
            .flatMapLatest { shortcut ->
                if (shortcut == null) {
                    flowOf(emptyList())
                } else {
                    ShortcutDao.queryShortcutByAppName(shortcut.appName)
                }
            }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCurrentAppNameByCurrentShortcut(): Flow<String> {
        return currentShortcut.flatMapLatest { shortcut ->
            if (shortcut == null) {
                flowOf("")
            } else {
                flowOf(shortcut.appName)
            }
        }
    }


    fun changeCurrentPreferenceShortcutId(newShortcutId: Int) {
        viewModelScope.launch {
            UserPreference.saveCurrentShortcutIdPreference(newShortcutId)
        }
    }

    fun changeCurrentShortcut(newShortcut: Shortcut) {
        currentShortcut.value = newShortcut
        viewModelScope.launch {
            UserPreference.saveCurrentShortcutIdPreference(newShortcut.id)
        }
    }

    fun addUserInput(input: String) {
        val currentList = userInput.value ?: mutableListOf()
        currentList.add(input)
        userInput.value = currentList
    }

    fun removeUserInput(input: String) {
        val currentList = userInput.value ?: mutableListOf()
        currentList.remove(input)
        userInput.value = currentList
    }

    fun removeUserInputLastOrNull() {
        val currentList = userInput.value ?: mutableListOf()
        if (currentList.isNotEmpty()) {
            currentList.removeAt(currentList.size - 1)
            userInput.value = currentList
        }
    }

    fun updateSearchStr(newSearchStr: String) {
        searchStr.value = newSearchStr
    }

    fun addShortcutHistory(shortcut: Shortcut) {
        // 获取当前的 List，如果是 null，则使用空列表
        val currentList = shortcutHistory.value ?: emptyList()

        // 添加新的 Shortcut 到列表中
        val updatedList = currentList + shortcut

        // 设置更新后的列表回 MutableLiveData
        shortcutHistory.value = updatedList
    }

    //暴漏出UserPreference的saveOsPreference
    fun saveOsPreference(value: KeyboardType) {
        viewModelScope.launch {
            UserPreference.saveKeyboardTypePreference(value)
        }
    }


    //切换到currentRecommendAppList的下一个App

    fun onCorrectInputShortcut() {
        addShortcutHistory(currentShortcut.value!!)

    }

    fun changeKeyboardType(newKeyboardType: KeyboardType) {
        currentKeyboardType.value = newKeyboardType
        viewModelScope.launch {
            UserPreference.saveKeyboardTypePreference(newKeyboardType)
        }
    }

    fun changeCurrentApp(newAppName: String) {
        //set the currentShortcut to the first shortcut in the app
        viewModelScope.launch {
            ShortcutDao.queryFirstShortcutByAppName(newAppName).collect { shortcut ->
                changeCurrentShortcut(shortcut!!)
            }
        }
    }

    fun changeCurrentGroup(newGroupName: String) {
        //set the currentShortcut to the first shortcut in the app
        viewModelScope.launch {
            ShortcutDao.queryFirstShortcutByAppNameAndGroupName(
                currentShortcut.value!!.appName, newGroupName
            ).collect { shortcut ->
                changeCurrentShortcut(shortcut!!)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ShortcutDao.closeRealm() // 关闭 Realm 实例
    }
}

