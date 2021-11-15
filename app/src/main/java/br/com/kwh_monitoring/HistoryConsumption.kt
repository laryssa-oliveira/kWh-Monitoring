package br.com.kwh_monitoring

data class HistoryConsumption (
    val kWh: String? = ""
)

data class DateHour(
    val dateHour: String? = ""
)

data class ConsumptionKW (
    val kW: Float? = 0.0f
)

data class ConsumptionTotal(
    val consumption: String? = ""
)

data class ListDay (
    val day: Int? = 0
)

data class ListPower (
    val kW: Float? = 0.0f
)