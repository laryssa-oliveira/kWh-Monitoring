package br.com.kwh_monitoring

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TableAdapter : RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    private var consumption: List<ConsumptionKW> = emptyList()
    private var date: List<DateHour> = emptyList()

    inner class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(historyConsumption: ConsumptionKW, date: DateHour) {
            itemView.findViewById<TextView>(R.id.dateFirebase).text =
                date.dateHour?.formatDateHour(date.dateHour)
            itemView.findViewById<TextView>(R.id.kWhFirebase).text =
                historyConsumption.kW.toString()
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
}