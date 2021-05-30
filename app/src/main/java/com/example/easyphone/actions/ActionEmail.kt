package com.example.easyphone.actions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.easyphone.actions.utils.ButtonAction
import com.example.easyphone.data.utils.StringDataPatternSubstitutor
import java.lang.Error

class ActionEmail(val args: Map<String, String>): ButtonAction {
    override fun performAction(context: Context) {
        val email = args["email"]
        val subject = args["subject"]
        val emailText = args["text"]
        if (email == null || subject == null || emailText == null) throw Error("Not enough args")


        val strSubstitutor = StringDataPatternSubstitutor(context)
        strSubstitutor.launchWithString(emailText) { str ->
            Log.d("MY_DEBUG", "intent created");
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_TEXT, str)
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                Log.d("MY_DEBUG", "activity resolved");
                context.startActivity(intent)
            }
        }

        //context.startActivity(intent)
    }
}