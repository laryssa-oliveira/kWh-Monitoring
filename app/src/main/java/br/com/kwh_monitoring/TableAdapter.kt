package br.com.kwh_monitoring

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class TableAdapter : RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    private var consumption: List<ConsumptionKW> = emptyList()
    private var date: List<DateHour> = emptyList()

    inner class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(consumptionKW: ConsumptionKW, dateHour: DateHour) {
            itemView.findViewById<TextView>(R.id.dateFirebase).text =
                formatDateHour(dateHour.dateTime!!)

            val decimalFormat3 = DecimalFormat("#.###")
            val formatStr = decimalFormat3.format(consumptionKW.kW).replaceAfter(".", ",")
            itemView.findViewById<TextView>(R.id.kWhFirebase).text = formatStr
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TableViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_table, parent, false)
        return TableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.bind(consumption[position], date[position])
    }

    override fun getItemCount(): Int {
        return consumption.size
    }

    fun setItems(listConsumption: List<ConsumptionKW>, listDateHour: List<DateHour>) {
        consumption = listConsumption
        date = listDateHour
    }

    fun formatDateHour(format: String): String? {
        val year: String =
            format[0].toString() + format[1].toString() + format[2].toString() + format[3].toString()
        val month: String = format[5].toString() + format[6].toString()
        val day: String = format[8].toString() + format[9].toString()
        val hour: String =
            format[11].toString() + format[12].toString() + format[13].toString() + format[14].toString() + format[15].toString() + format[16].toString() + format[17].toString() + format[18].toString()
        val total = day.plus("/").plus(month).plus("/").plus(year).plus("    ").plus(hour)
        return total
    }
}