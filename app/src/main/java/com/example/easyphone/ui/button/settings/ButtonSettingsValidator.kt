package com.example.easyphone.ui.button.settings

import android.content.res.Resources
import com.example.easyphone.R

class ButtonSettingsValidator(val resources: Resources) {
    fun validateHeight(value: Int): String? {
        if (value == 0) {
            return(resources.getString(R.string.row_error_0))
        }
        if (value > resources.getInteger(R.integer.row_number) / 2) {
            return(resources.getString(R.string.row_error_big))
        }
        return null
    }

    fun validateWidth(value: Int): String? {
        if (value == 0) {
            return(resources.getString(R.string.column_error_0))
        }
        if (value > resources.getInteger(R.integer.column_number)) {
            return(resources.getString(R.string.column_error_big))
        }
        return null
    }

    fun validateText(string: String): String? {
        return null
    }
}