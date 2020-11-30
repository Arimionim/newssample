package com.aknazarov.newssample

import java.text.DateFormat.getDateTimeInstance
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        fun getTime(time: String?): String {
            if (time == null) {
                return ""
            }

            val formatterFrom = SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss ZZZ", Locale.ROOT)
            val formatterTo = getDateTimeInstance()

            return try {
                formatterTo.format(formatterFrom.parse(time) ?: "")
            } catch (e: ParseException) {
                time
            }
        }
    }
}