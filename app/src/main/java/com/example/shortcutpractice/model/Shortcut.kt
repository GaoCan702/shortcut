package com.example.shortcutpractice.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class Shortcut(
    @PrimaryKey var id: Int = 0,
    var key_combo: String = "",
    var description: String = "",
    var group_name: String = ""
) : RealmObject
