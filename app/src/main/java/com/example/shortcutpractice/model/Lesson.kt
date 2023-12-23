package com.example.shortcutpractice.model

import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class Lesson(
    @PrimaryKey var id: Int = 0,
    var order_id: Int? = null,
    var course_name: String = "",
    var title: String = "",
    var shortcuts: RealmList<Shortcut>? = null
) : RealmObject
