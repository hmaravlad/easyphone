package com.example.easyphone.actions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.easyphone.actions.utils.ButtonAction
import com.example.easyphone.data.utils.StringDataPatternSubstitutor
import java.lang.Error

class ActionToast(val args: Map<String, String>): ButtonAction {
    override fun performAction(context: Context) {
        val text = args["text"]
        if (text == null) throw Error("Not enough args")

        val duration = if (text.length < 30) { Toast.LENGTH_SHORT } else { Toast.LENGTH_LONG }

        Log.d("MY_DEBUG", "EMMM $str")
        val toast = Toast.makeText(context, str, duration)
        toast.show()
    }
}