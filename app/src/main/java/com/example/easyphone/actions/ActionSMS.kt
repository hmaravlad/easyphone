package com.example.easyphone.actions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.easyphone.actions.utils.ButtonAction
import com.example.easyphone.data.utils.StringDataPatternSubstitutor
import java.lang.Error

class ActionSMS(val args: Map<String, String>): ButtonAction {
    override fun performAction(context: Context) {
        val phoneNumber = args["number"]
        val smsText = args["text"]
        if (phoneNumber == null || smsText == null) throw Error("Not enough args")

        Log.d("MY_DEBUG", "number:${phoneNumber}");


        val strSubstitutor = StringDataPatternSubstitutor(context)
        strSubstitutor.launchWithString(smsText) { str ->

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:${phoneNumber}")
                putExtra("sms_body", str)
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                Log.d("MY_DEBUG", "activity resolved");
                context.startActivity(intent)
            }
        }
    }
}