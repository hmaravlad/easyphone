package com.example.easyphone.actions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.easyphone.actions.utils.ButtonAction
import java.lang.Error

class ActionApp(val args: Map<String, String>): ButtonAction {
    override fun performAction(context: Context) {
        val packageName = args["app"]
        if (packageName == null) throw Error("No phone number argument")

        val intent = Intent(Intent.ACTION_MAIN)
        intent.setPackage(packageName)
        Log.d("MY_DEBUG", "intent created")

        try {
            Log.d("MY_DEBUG", "activity resolved");
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.d("MY_DEBUG", "activity not found");
        }
    }
}