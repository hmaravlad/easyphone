package com.example.easyphone.actions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.easyphone.actions.utils.ButtonAction
import java.lang.Error

class ActionCall(val args: Map<String, String>): ButtonAction {
    override fun performAction(context: Context) {
        val phoneNumber = args["number"]
        if (phoneNumber == null) throw Error("No phone number argument")

        Log.d("MY_DEBUG", "number:${phoneNumber}");

        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${phoneNumber}")
        }
        Log.d("MY_DEBUG", "intent created");
        if (intent.resolveActivity(context.packageManager) != null) {
            Log.d("MY_DEBUG", "activity resolved");
            context.startActivity(intent)
        }
        // context.startActivity(intent)
    }
}