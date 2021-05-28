package com.example.easyphone.actions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.easyphone.actions.utils.ButtonAction
import java.lang.Error

class ActionURL(val args: Map<String, String>): ButtonAction {
    override fun performAction(context: Context) {
        val url = args["url"]
        if (url == null) throw Error("Not enough args")

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        Log.d("MY_DEBUG", "intent created");
        if (intent.resolveActivity(context.packageManager) != null) {
            Log.d("MY_DEBUG", "activity resolved");
            context.startActivity(intent)
        }
        //context.startActivity(intent)
    }
}