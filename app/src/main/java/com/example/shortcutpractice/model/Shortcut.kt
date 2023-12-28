package com.example.shortcutpractice.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
open class Shortcut : RealmObject {
    @PrimaryKey
    var id: Int = 0 //primary key
    var appName: String = ""  //Visual Studio Code
    var appNameCn: String = ""  //app name in chinese
    var keyCombo: String = ""
    var keyComboMac: String = ""
    var desc: String = ""
    var groupName: String = ""
    var groupOrder: Int = 0
    var shortcutOrder: Int = 0
    var descCn: String = ""
    var groupNameCn: String = ""
    var deleted: Boolean = false
}
