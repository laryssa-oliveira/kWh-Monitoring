package br.com.kwh_monitoring

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.Entry
/*

class DatePower : ArrayList<Entry>.Adapter<DatePower.DatePowerViewHolder> {

    private var consumptionKW: List<ConsumptionKW> = emptyList()
    private var dateHour: List<DateHour> = emptyList()

    fun setItems(listConsumption: List<ConsumptionKW>, listDateHour: List<DateHour>) {
        consumptionKW = listConsumption
        dateHour = listDateHour
    }

}

: RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    private var consumption: List<HistoryConsumption> = emptyList()
    private var date: List<DateHour> = emptyList()

    inner class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(historyConsumption: HistoryConsumption, date: DateHour) {
            itemView.findViewById<TextView>(R.id.dateFirebase).text =
                date.dateHour?.formatDateHour(date.dateHour)
            itemView.findViewById<TextView>(R.id.kWhFirebase).text =
                historyConsumption.kWh
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

    fun setItems(listConsumption: List<HistoryConsumption>, listDateHour: List<DateHour>) {
        consumption = listConsumption
        date = listDateHour
    }

 */