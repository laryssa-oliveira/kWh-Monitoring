package br.com.kwh_monitoring

//yyyy-mm-dd to dd/mm/yyyy
fun String.formatDate(format: String): String? {
    val year: String = format[0].toString() + format[1].toString() + format[2].toString() + format[3].toString()
    val month: String = format[5].toString() + format[6].toString()
    val day: String = format[8].toString() + format[9].toString()
    val hour: String = format[11].toString() + format[12].toString() + format[13].toString() + format[14].toString() + format[15].toString() + format[16].toString() + format[17].toString() + format[18].toString()
    return "$day/$month/$year      $hour"
}