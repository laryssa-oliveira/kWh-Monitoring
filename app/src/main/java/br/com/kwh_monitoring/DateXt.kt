package br.com.kwh_monitoring

//yyyy-mm-dd to dd/mm/yyyy
fun String.formatDateHour(format: String): String? {
    val year: String = format[0].toString() + format[1].toString() + format[2].toString() + format[3].toString()
    val month: String = format[5].toString() + format[6].toString()
    val day: String = format[8].toString() + format[9].toString()
    val hour: String = format[11].toString() + format[12].toString() + format[13].toString() + format[14].toString() + format[15].toString() + format[16].toString() + format[17].toString() + format[18].toString()
    return "$day/$month/$year      $hour"
}

fun String.formatDate(format: String): String? {
    val year: String = format[0].toString() + format[1].toString() + format[2].toString() + format[3].toString()
    val month: String = format[5].toString() + format[6].toString()
    val day: String = format[8].toString() + format[9].toString()
    return "$day/$month/$year"
}

fun String.formatSchedule(format: String): String? {
    val hour: String = format[11].toString() + format[12].toString()
    val minute: String = format[14].toString() + format[15].toString()
    val second: String = format[17].toString() + format[18].toString()
    return "$hour:$minute:$second"
}

fun String.formatHour(format: String): Int? {
    val hourFormat = format.split("T", ":")
    val hourStr = hourFormat[1]
    return hourStr.toInt()
}

fun String.formatMinute(format: String): Int? {
    val minuteFormat = format.split("T", ":")
    val minuteStr = minuteFormat[2]
    return minuteStr.toInt()
}

fun String.formatSecond(format: String): Int? {
    val secondFormat = format.split("T", ":", "Z")
    val secondStr = secondFormat[3]
    return secondStr.toInt()
}

fun String.formatDay(format: String): Int? {
    val dayFormat = format.split("-", "T")
    val dayStr = dayFormat[2]
    return dayStr.toInt()
}

fun String.formatPower(format: String): Float? {
    val powerFormat = format.split("Z").toTypedArray()
    val powerStr = powerFormat[1]
    return powerStr.toFloat()
}
