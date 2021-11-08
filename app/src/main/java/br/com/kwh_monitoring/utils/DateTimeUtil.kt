package br.com.kwh_monitoring.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {
    fun convertDateStringToMillis(strDate: String?): Long {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return try {
            simpleDateFormat.parse(strDate).time
        } catch (ex: ParseException) {
            ex.printStackTrace()
            0
        }
    }

    fun convertFloatDateToStringDate(floatDate: Float): String {
        val date = Date(floatDate.toLong())
        val dateFormat: DateFormat = SimpleDateFormat("MMM-dd-yyyy")
        return dateFormat.format(date)
    }

    fun convertFloatDateToMonth(floatDate: Float): String {
        val date = Date(floatDate.toLong())
        val dateFormat: DateFormat = SimpleDateFormat("MMM")
        return dateFormat.format(date)
    }

    fun convertFloatDateToQuarter(floatDate: Float): String {
        val date = Date(floatDate.toLong())
        val dateFormat: DateFormat = SimpleDateFormat("MMM")
        val rs = dateFormat.format(date)
        if (rs == "Mar") {
            return "Quarter I"
        } else if (rs == "Jun") {
            return "Quarter II"
        } else if (rs == "Sep") {
            return "Quarter III"
        } else if (rs == "Dec") {
            return "Quarter IV"
        }
        return ""
    }

    fun convertStringDateToDate(strDate: String?): Date? {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return try {
            simpleDateFormat.parse(strDate)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    fun isTheLastDayOfMonth(date: Date?): Boolean {
        if (date != null) {
            val c = Calendar.getInstance()
            c.time = date
            val day = c[Calendar.DAY_OF_MONTH]
            val month = c[Calendar.MONTH] + 1
            val year = c[Calendar.YEAR]
            if (day < 28) {
                return false
            }
            if (month == 2) {
                if (isLeapYear(year)) {
                    if (day == 29) {
                        return true
                    }
                } else {
                    if (day == 28) {
                        return true
                    }
                }
            } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                if (day == 31) {
                    return true
                }
            } else {
                if (day == 30) {
                    return true
                }
            }
            return false
        }
        return false
    }

    fun isLeapYear(year: Int): Boolean {
        return (year % 400 == 0 || year % 4 == 0 && year % 100 != 0)
    }

    fun isQuarterMonth(month: Int): Boolean {
        return month == 3 || month == 6 || month == 9 || month == 12
    }

    fun isQuarterMonth(date: Date?): Boolean {
        val c = Calendar.getInstance()
        c.time = date
        val month = c[Calendar.MONTH] + 1
        return month == 3 || month == 6 || month == 9 || month == 12
    }
}