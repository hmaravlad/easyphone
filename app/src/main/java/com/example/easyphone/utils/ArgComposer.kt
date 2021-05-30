package com.example.easyphone.utils

import com.example.easyphone.db.entities.ActionArg
import java.util.*

object ArgComposer {
    fun compose(list: List<ActionArg>): Map<String, String> {
        val res: MutableMap<String, String> = mutableMapOf()

        list.forEach {
            res.put(it.type, it.value.toLowerCase(Locale.UK))
        }

        return res
    }
}