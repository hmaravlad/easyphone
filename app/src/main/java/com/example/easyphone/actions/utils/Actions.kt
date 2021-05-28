package com.example.easyphone.actions.utils

import android.text.InputType
import com.example.easyphone.actions.*

object Actions {
    val data = mapOf(
        "toast" to ActionData(
            "toast",
            listOf(
                ActionArgData(
                    "text",
                    "Text",
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE
                )
            )
        ) { args -> ActionToast(args) },
        "call" to ActionData(
            "call",
            listOf(
                ActionArgData(
                    "number",
                    "Number",
                    InputType.TYPE_CLASS_PHONE
                )
            )
        ) { args -> ActionCall(args) },
        "sms" to ActionData(
            "sms",
            listOf(
                ActionArgData(
                    "number",
                    "Number",
                    InputType.TYPE_CLASS_PHONE

                ),
                ActionArgData(
                    "text",
                    "Text",
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE
                )
            )
        ) { args -> ActionSMS(args) },
        "email" to ActionData(
            "email",
            listOf(
                ActionArgData(
                    "email",
                    "Email",
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                ),
                ActionArgData(
                    "subject",
                    "Subject",
                    InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT
                ),
                ActionArgData(
                    "text",
                    "Text",
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE
                )
            )
        ) { args -> ActionEmail(args) },
        "url" to ActionData(
            "url",
            listOf(
                ActionArgData(
                    "url",
                    "Url",
                    InputType.TYPE_TEXT_VARIATION_URI
                )
            )
        ) { args -> ActionURL(args) },
        "app" to ActionData(
            "app",
            listOf()
        ) { args -> ActionApp(args) }
    )
}