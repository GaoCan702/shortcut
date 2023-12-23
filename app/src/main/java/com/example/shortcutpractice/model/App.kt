package com.example.shortcutpractice.model

import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class App(
    @PrimaryKey var id: Int = 0,
    var order_id: Int = 0,
    var name: String = "",
    var icon: String = "",
    var description: String? = null,
    var lessons: RealmList<Lesson>? = null
) : RealmObject
