package br.com.kwh_monitoring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.kwh_monitoring.databinding.FragmentTableBinding
import com.github.mikephil.charting.data.Entry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TableFragment : Fragment() {

    private lateinit var binding: FragmentTableBinding
    private val adapter by lazy { TableAdapter() }
    private val database = Firebase.database
    private var databaseRef: DatabaseReference = database.reference.child("chartTable")
    private var powerRef: DatabaseReference = database.reference.child("ap_power")
    private val referenceDate: DatabaseReference = database.reference.child("date_hour")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listConsumption = ArrayList<HistoryConsumption>()
        val listDate = ArrayList<DateHour>()
        var kWh: Float?

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listConsumption.clear()
                for (snapshot in dataSnapshot.children) {
                    val consumption: Consumption? = snapshot.getValue(Consumption::class.java)
                    kWh = consumption?.getkWh()
                    listConsumption.add(HistoryConsumption(kWh.toString()))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) { }
        })

        val listConsumptionKW = ArrayList<ConsumptionKW>()
        powerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listConsumptionKW.clear()
                for (snapshot in dataSnapshot.children) {
                    val apPower: Float? = snapshot.getValue(Float::class.java)
                    val power = apPower?.div(1000)
                    listConsumptionKW.add(ConsumptionKW(power))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        referenceDate.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listDate.clear()
                for (snapshot in dataSnapshot.children) {
                    val date: String? = snapshot.getValue(String::class.java)
                    listDate.add(DateHour(date))
                    adapter.setItems(listConsumptionKW, listDate)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) { }
        })
/*
        var day: Int? = 0
        var day2: Int? = 0
        var hour: Int? = 0
        var hour2: Int? = 0
        var kWh: Float? = 0.0f
        var sumKWh: Float? = 0.0f
        var listConsumption: Consumption
        val list = ArrayList<Entry>()
        list.clear()

        for (i in 0..listConsumptionKW.size){
            listDateHour[i].dateHour?.let { day = listDateHour[i].dateHour?.formatDay(it) }
            listDateHour[i+1].dateHour?.let { day2 = listDateHour[i].dateHour?.formatDay(it) }
            listDateHour[i].dateHour?.let { hour = listDateHour[i].dateHour?.formatHour(it) }
            listDateHour[i+1].dateHour?.let { hour2 = listDateHour[i].dateHour?.formatHour(it) }
            if(day == day2){
                if (hour == hour2) {
                    kWh = (listConsumptionKW[i].kW?.plus(kWh!!))
                } else{
                    kWh = (listConsumptionKW[i].kW?.plus(kWh!!))
                }
                sumKWh = (kWh?.plus(sumKWh!!))
            } else {
                kWh = listConsumptionKW[i].kW
            }
        }

 */

        binding.recyclerView.adapter = adapter

    }



}