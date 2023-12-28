import com.example.shortcutpractice.model.Shortcut
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.SingleQueryChange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


object ShortcutDao {

    val realm = Realm.open(
        RealmConfiguration.Builder(schema = setOf(Shortcut::class))
            .deleteRealmIfMigrationNeeded()
            .initialData {
                copyToRealm(
                    Shortcut().apply {
                        id = 1
                        appName = "Visual Studio Code"
                        appNameCn = "Visual Studio Code"
                        keyCombo = "Ctrl + Shift + P"
                        keyComboMac = "Cmd + Shift + P"
                        desc = "Show Command Palette"
                        groupName = "Basic editing"
                        groupOrder = 1
                        shortcutOrder = 1
                        descCn = "显示命令面板"
                        groupNameCn = "基本编辑"
                        deleted = false
                    })
                copyToRealm(
                    Shortcut().apply {
                        id = 2
                        appName = "Visual Studio Code"
                        appNameCn = "Visual Studio Code"
                        keyCombo = "Ctrl + P"
                        keyComboMac = "Cmd + P"
                        desc = "Quick Open, Go to File..."
                        groupName = "Basic editing"
                        groupOrder = 1
                        shortcutOrder = 2
                        descCn = "快速打开，转到文件..."
                        groupNameCn = "基本编辑"
                        deleted = false
                    }
                )
                copyToRealm(
                    Shortcut().apply {
                        id = 3
                        appName = "Visual Studio Code"
                        appNameCn = "Visual Studio Code"
                        keyCombo = "Ctrl + Shift + N"
                        keyComboMac = "Cmd + Shift + N"
                        desc = "New window/instance"
                        groupName = "Basic editing"
                        groupOrder = 1
                        shortcutOrder = 3
                        descCn = "新建窗口/实例"
                        groupNameCn = "基本编辑"
                        deleted = false
                    }
                )

                copyToRealm(
                    Shortcut().apply {
                        id = 4
                        appName = "Visual Studio Code"
                        appNameCn = "Visual Studio Code"
                        keyCombo = "Ctrl + Shift + W"
                        keyComboMac = "Cmd + Shift + W"
                        desc = "Close window/instance"
                        groupName = "Basic editing"
                        groupOrder = 1
                        shortcutOrder = 4
                        descCn = "关闭窗口/实例"
                        groupNameCn = "基本编辑"
                        deleted = false
                    }
                )
                copyToRealm(
                    Shortcut().apply {
                        id = 5
                        appName = "Visual Studio Code"
                        appNameCn = "Visual Studio Code"
                        keyCombo = "Ctrl + X"
                        keyComboMac = "Cmd + X"
                        desc = "Cut line (empty selection)"
                        groupName = "Basic editing"
                        groupOrder = 1
                        shortcutOrder = 5
                        descCn = "剪切行（空选择）"
                        groupNameCn = "基本编辑"
                        deleted = false
                    }
                )
                copyToRealm(
                    Shortcut().apply {
                        id = 6
                        appName = "Visual Studio Code"
                        appNameCn = "Visual Studio Code"
                        keyCombo = "Ctrl + C"
                        keyComboMac = "Cmd + C"
                        desc = "Copy line (empty selection)"
                        groupName = "Basic editing"
                        groupOrder = 1
                        shortcutOrder = 6
                        descCn = "复制行（空选择）"
                        groupNameCn = "基本编辑"
                        deleted = false
                    }
                )

                copyToRealm(
                    //chrome
                    Shortcut().apply {
                        id = 7
                        appName = "Google Chrome"
                        appNameCn = "谷歌浏览器"
                        keyCombo = "Ctrl + Shift + N"
                        keyComboMac = "Cmd + Shift + N"
                        desc = "Open a new window in incognito mode"
                        groupName = "Basic editing"
                        groupOrder = 1
                        shortcutOrder = 7
                        descCn = "在隐身模式下打开新窗口"
                        groupNameCn = "基本编辑"
                        deleted = false
                    }
                )
                copyToRealm(
                    Shortcut().apply {
                        id = 8
                        appName = "Google Chrome"
                        appNameCn = "谷歌浏览器"
                        keyCombo = "Ctrl + Shift + T"
                        keyComboMac = "Cmd + Shift + T"
                        desc =
                            "Reopen the last tab you've closed. Google Chrome remembers the last 10 tabs you've closed"
                        groupName = "Basic editing"
                        groupOrder = 1
                        shortcutOrder = 8
                        descCn = "重新打开您最近关闭的标签页。谷歌浏览器会记住您最近关闭的10个标签页"
                        groupNameCn = "基本编辑"
                        deleted = false
                    }
                )
                copyToRealm(
                    Shortcut().apply {
                        id = 9
                        appName = "Google Chrome"
                        appNameCn = "谷歌浏览器"
                        keyCombo = "Ctrl + Shift + W"
                        keyComboMac = "Cmd + Shift + W"
                        desc = "Close the current window"
                        groupName = "Basic editing"
                        groupOrder = 1
                        shortcutOrder = 9
                        descCn = "关闭当前窗口"
                        groupNameCn = "基本编辑"
                        deleted = false
                    }
                )

            }
            .build()
    )

    fun queryShortcut(shortcutId: Int): Flow<SingleQueryChange<Shortcut>> {
        // Use query API to listen for data changes
        val query = realm.query<Shortcut>("id='$shortcutId'")
        return query.first().asFlow()
    }

    fun queryApps(searchStr: String): Flow<List<String>> {
        val query = if (searchStr.isBlank()) {
            realm.query<Shortcut>().sort("appName")
        } else {
            realm.query<Shortcut>("UPPER(appName) LIKE UPPER('%$searchStr%')").sort("appName")
        }
        return query.find().asFlow().map { change ->
            change.list.map { it.appName }.distinct()
        }
    }


    //query first shortcut, order by appName and group_name
    fun queryFirstShortcut(): Flow<SingleQueryChange<Shortcut>> {
        // Use query API to listen for data changes
        val query = realm.query<Shortcut>()
            .sort("appName")
            .sort("groupOrder")
            .sort("shortcutOrder")
        return query.first().asFlow()
    }

    //query app's first shortcut, order by group_name and shortcut_order
    fun queryFirstShortcutByAppName(appName: String): Flow<Shortcut?> {
        // Use query API to listen for data changes
        val query = realm.query<Shortcut>("appName='$appName'")
            .sort("groupOrder")
            .sort("shortcutOrder")
        return query.first().asFlow().map { change ->
            change.obj
        }
    }

    fun queryFirstShortcutByAppNameAndGroupName(
        appName: String,
        groupName: String
    ): Flow<Shortcut?> {
        // Use query API to listen for data changes
        val query = realm.query<Shortcut>("appName='$appName' AND groupName='$groupName'")
            .sort("groupOrder")
            .sort("shortcutOrder")
        return query.first().asFlow().map { change ->
            change.obj
        }
    }

    //query shortcut list by appName, order by group_name and shortcut_order,distinct
    fun queryShortcutByAppName(appName: String): Flow<List<Shortcut>> {
        val query = realm.query<Shortcut>("appName='$appName'")
            .sort("groupOrder")
            .sort("shortcutOrder")
        return query.find().asFlow().map { change ->
            change.list.distinct()
        }
    }

    //query groupName by appName, order by groupOrder,distinct
    fun queryGroupNameByAppName(appName: String): Flow<List<String>> {
        val query = realm.query<Shortcut>("appName='$appName'")
            .sort("groupOrder")
        return query.find().asFlow().map { change ->
            change.list.map { it.groupName }.distinct()
        }
    }

    // Close realm when app is closed
    fun closeRealm() {
        realm.close()
    }
}
