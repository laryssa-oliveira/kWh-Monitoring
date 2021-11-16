package br.com.kwh_monitoring

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.kwh_monitoring.databinding.FragmentMainBinding
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val database = Firebase.database
    private val referenceStatus = database.getReference("control_status")
    private var powerRef: DatabaseReference = database.reference.child("ap_power")
    private val dateHourRef: DatabaseReference = database.reference.child("date_hour")
    var lineDataSet = LineDataSet(null, null)
    var iLineDataSets: ArrayList<ILineDataSet> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.off.setOnClickListener { referenceStatus.setValue("OFF") }
        binding.on.setOnClickListener { referenceStatus.setValue("ON") }

        binding.lineChart.setNoDataText("Carregando...")

        val listConsumptionKW = ArrayList<ConsumptionKW>()
        val listDate = ArrayList<DateHour>()
        powerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listConsumptionKW.clear()
                for (snapshot in dataSnapshot.children) {
                    val apPower: Float? = snapshot.getValue(Float::class.java)
                    val power = apPower?.div(6000)
                    listConsumptionKW.add(ConsumptionKW(power))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        dateHourRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listDate.clear()
                for (snapshot in dataSnapshot.children) {
                    val dateHour: String? = snapshot.getValue(String::class.java)
                    listDate.add(DateHour(dateHour))
                }
                calculateKWH(listConsumptionKW, listDate)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


    }

    fun calculateKWH(listPower: ArrayList<ConsumptionKW>, listDay: ArrayList<DateHour>) {
        val listDayInt = ArrayList<Int?>()
        listDayInt.clear()
        val listHourInt = ArrayList<Int?>()
        listHourInt.clear()
        val listMinuteInt = ArrayList<Int?>()
        listMinuteInt.clear()
        val listSecondInt = ArrayList<Int?>()
        listSecondInt.clear()
        val listHourTotal = ArrayList<Int?>()
        listHourTotal.clear()
        val list = ArrayList<Entry>()
        list.clear()
        var kWh: Float? = 0.0f
        var day: Int?
        var day2: Int? = 0
        var dateHour: DateHour
        var hour: Int?
        var minute: Int?
        var second: Int?
        var totalHour: Int? = 0
        val constError: Float? = 0.5f
        var totalKWH: Float? = 0.0f


        for (i in 0..(listPower.size - 1)) {
            dateHour = listDay[i]
            day = dateHour.dateHour?.formatDay(dateHour.dateHour!!)
            listDayInt.add(day)
            hour = dateHour.dateHour?.formatHour(dateHour.dateHour!!)
            listHourInt.add(hour)
            minute = dateHour.dateHour?.formatMinute(dateHour.dateHour!!)
            listMinuteInt.add(minute)
            second = dateHour.dateHour?.formatSecond(dateHour.dateHour!!)
            listSecondInt.add(second)
        }

        for (i in 1..listPower.size) {
            val day1 = listDayInt[i - 1]
            if (i < listDayInt.size) {
                day2 = listDayInt[i]
            }
            if (day1 == day2) {
                if (i == listDayInt.size - 1) {
                    totalHour = totalHour?.plus(listHourInt[i - 1]?.times(3600)!!)!!.plus(listMinuteInt[i - 1]!!.times(60)!!.plus(listSecondInt[i - 1]!!)).plus(listHourInt[i]?.times(3600)!!)!!.plus(listMinuteInt[i]!!.times(60)!!.plus(listSecondInt[i ]!!))
                    val mod = totalHour.mod(3600)
                    kWh = listPower[i - 1].kW?.plus(listPower[i].kW!!)
                    kWh = kWh?.times(mod)?.div(3600)?.times(constError!!)
                    totalKWH = totalKWH?.plus(kWh!!)
                    val dataPoint = DataPoint(day1, kWh)
                    list.add(Entry(dataPoint.getxValue().toFloat(), dataPoint.getyValue()))
                    showChart(list)
                    kWh = 0.0f
                }
                totalHour = totalHour?.plus(listHourInt[i - 1]?.times(3600)!!)!!.plus(listMinuteInt[i - 1]!!.times(60)!!.plus(listSecondInt[i - 1]!!))
                kWh = listPower[i - 1].kW?.plus(kWh!!)
            } else {
                totalHour = totalHour?.plus(listHourInt[i - 1]?.times(3600)!!)!!.plus(listMinuteInt[i - 1]!!.times(60)!!.plus(listSecondInt[i - 1]!!))
                val mod = totalHour.mod(3600)
                kWh = listPower[i - 1].kW?.plus(kWh!!)
                kWh = kWh?.times(mod)?.div(3600)?.times(constError!!)
                totalKWH = totalKWH?.plus(kWh!!)
                val dataPoint = DataPoint(day1, kWh)
                list.add(Entry(dataPoint.getxValue().toFloat(), dataPoint.getyValue()))
                showChart(list)
                kWh = 0.0f
                totalHour = 0
            }

        }

        binding.consumption.text = totalKWH.toString()
        val value = totalKWH?.times(0.6)
        binding.value.text = "R$ " + value.toString()


    }

    private fun showChart(dataVals: ArrayList<Entry>) {
        lineDataSet.values = dataVals
        dataVals.size
        lineDataSet.label = "Consumo em kWh"
        iLineDataSets.clear()
        lineDataSet.color = Color.rgb(61, 224, 242)
        lineDataSet.lineWidth = 2.5f
        lineDataSet.setCircleColor(Color.rgb(61, 224, 242))
        lineDataSet.circleRadius = 5f
        lineDataSet.fillColor = Color.rgb(61, 224, 242)
        lineDataSet.circleHoleColor = Color.rgb(61, 224, 242)
        lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        lineDataSet.setDrawValues(true)
        lineDataSet.valueTextSize = 10f
        lineDataSet.valueTextColor = Color.rgb(61, 224, 242)

        binding.lineChart.setBackgroundColor(Color.BLACK)
        binding.lineChart.setDrawGridBackground(false)
        binding.lineChart.setDrawBorders(true)
        binding.lineChart.setBorderWidth(2.5F)
        binding.lineChart.setBorderColor(Color.rgb(26, 29, 38))

        val description = Description()
        description.text = ""
        binding.lineChart.description = description

        val legend = binding.lineChart.legend
        legend.isEnabled = true
        legend.textColor = Color.rgb(61, 224, 242)
        legend.textSize = 10F
        legend.form = Legend.LegendForm.CIRCLE
        legend.formSize = 5F

        val xAxis = binding.lineChart.xAxis
        val yAxis = binding.lineChart.axisLeft
        val yAxisRight = binding.lineChart.axisRight

        xAxis.setLabelCount(3, true)
        xAxis.axisLineColor = Color.BLACK
        xAxis.textColor = Color.rgb(61, 224, 242)
        xAxis.gridColor = Color.BLACK
        yAxis.axisLineColor = Color.BLACK
        yAxis.textColor = Color.rgb(61, 224, 242)
        yAxis.gridColor = Color.BLACK
        yAxisRight.axisLineColor = Color.BLACK
        yAxisRight.textColor = Color.BLACK
        yAxisRight.gridColor = Color.BLACK

        iLineDataSets.add(lineDataSet)
        val lineData = LineData(iLineDataSets)
        binding.lineChart.clear()
        binding.lineChart.data = lineData
        binding.lineChart.invalidate()
    }

}

