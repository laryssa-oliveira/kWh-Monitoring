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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.joda.time.*

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val database = Firebase.database
    private val referenceStatus = database.getReference("control_status")
    private var powerRef: DatabaseReference = database.reference.child("power")
    private val dateHourRef: DatabaseReference = database.reference.child("date_time")
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
                    val power = apPower?.div(1000)
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

    fun calculateKWH(listPower: ArrayList<ConsumptionKW>, listDateHour: ArrayList<DateHour>) {
        val list = ArrayList<Entry>()
        list.clear()
        var dateTime1: DateTime
        var dateTime2 = DateTime(listDateHour[0].dateTime)
        var totalH = 0
        var totalM = 0
        var totalS = 0
        var totalTime = 0.0f
        var kWh: Float? = 0.0f
        var totalKwh: Float? = 0.0f

        for (i in 1..listPower.size) {
            dateTime1 = DateTime(listDateHour[i - 1].dateTime)

            if (i < listPower.size) {
                dateTime2 = DateTime(listDateHour[i].dateTime)
            }

            if (Days.daysBetween(dateTime1, dateTime2).days == 0) {

                if (i == listPower.size - 1) {
                    totalH = Hours.hoursBetween(dateTime1, dateTime2).hours
                    totalM = Minutes.minutesBetween(dateTime1, dateTime2).minutes
                    totalS = Seconds.secondsBetween(dateTime1, dateTime2).seconds
                    totalTime += totalH.div(24).plus(totalM.div(60)).plus(totalS)
                    kWh = kWh?.plus(listPower[i - 1].kW!!)!!.plus(listPower[i].kW!!)
                        .times(totalTime.div(3600)!!)
                    val dataPoint = DataPoint(dateTime1.dayOfMonth, kWh)
                    list.add(Entry(dataPoint.getxValue().toFloat(), dataPoint.getyValue()))
                    showChart(list)
                    totalKwh = totalKwh?.plus(kWh!!)
                    kWh = 0.0f
                    totalTime = 0.0f
                } else {
                    totalH = Hours.hoursBetween(dateTime1, dateTime2).hours
                    totalM = Minutes.minutesBetween(dateTime1, dateTime2).minutes
                    totalS = Seconds.secondsBetween(dateTime1, dateTime2).seconds
                    totalTime += totalH.div(24).plus(totalM.div(60)).plus(totalS)
                    kWh = kWh?.plus(listPower[i - 1].kW!!)!!
                }
            } else {
                kWh = kWh?.plus(listPower[i - 1].kW!!)!!.times(totalTime.div(3600)!!)
                val dataPoint = DataPoint(dateTime1.dayOfMonth, kWh)
                list.add(Entry(dataPoint.getxValue().toFloat(), dataPoint.getyValue()))
                showChart(list)
                totalKwh = totalKwh?.plus(kWh!!)
                kWh = 0.0f
                totalTime = 0.0f
            }

        }

        val formatNumber = formatValue(totalKwh?.formatNum(3)!!)
        binding.consumption.text = "$formatNumber kWh"
        val value = formatValue(totalKwh?.times(0.6)?.toFloat()?.formatNum(2)!!)
        binding.value.text = "R$ $value"

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

        xAxis.setLabelCount(1, true)
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

