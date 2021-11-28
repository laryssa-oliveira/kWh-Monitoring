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
import org.joda.time.tz.UTCProvider
import java.text.DecimalFormat


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
                    val powerFirebase: Float? = snapshot.getValue(Float::class.java)
                    val power = powerFirebase?.div(1000)
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
        var dateTime2 = DateTime(listDateHour[0].dateTime, DateTimeZone.UTC)
        var totalH = 0
        var totalM = 0
        var totalS = 0
        var totalTime = 0.0f
        var kWh: Float? = 0.0f
        var totalKwh: Double? = 0.0
        var qtdKwh = 0

        for (i in 1..listPower.size) {
            dateTime1 = DateTime(listDateHour[i - 1].dateTime, DateTimeZone.UTC)

            if (i < listPower.size) {
                dateTime2 = DateTime(listDateHour[i].dateTime, DateTimeZone.UTC)
            }

            if (dateTime1.dayOfMonth == dateTime2.dayOfMonth) {

                if (i == listPower.size - 1) {
                    if (listPower[i].kW == 0.0f) {
                        totalTime += 0
                        kWh = ((kWh?.plus(listPower[i - 1].kW!!)!!).times(totalTime.div(3600))).div(qtdKwh)
                        val dataPoint = DataPoint(dateTime1.dayOfMonth, kWh)
                        list.add(Entry(dataPoint.getxValue().toFloat(), dataPoint.getyValue()))
                        showChart(list)
                        totalKwh = totalKwh?.plus(kWh)
                        kWh = 0.0f
                        totalTime = 0.0f
                        qtdKwh = 0
                    } else {
                        totalH = Hours.hoursBetween(dateTime1, dateTime2).hours
                        totalM = Minutes.minutesBetween(dateTime1, dateTime2).minutes
                        totalS = Seconds.secondsBetween(dateTime1, dateTime2).seconds
                        qtdKwh +=1
                        totalTime += totalH.div(24).plus(totalM.div(60)).plus(totalS)
                        kWh = ((kWh?.plus(listPower[i - 1].kW!!)!!.plus(listPower[i].kW!!))
                            .times(totalTime.div(3600))).div(qtdKwh)
                        val dataPoint = DataPoint(dateTime1.dayOfMonth, kWh)
                        list.add(Entry(dataPoint.getxValue().toFloat(), dataPoint.getyValue()))
                        showChart(list)
                        totalKwh = totalKwh?.plus(kWh)
                        kWh = 0.0f
                        totalTime = 0.0f
                        qtdKwh = 0
                    }


                } else {
                    if (listPower[i - 1].kW == 0.0f) {
                        totalTime += 0
                        kWh = kWh?.plus(0)!!
                    } else {
                        totalH = Hours.hoursBetween(dateTime1, dateTime2).hours
                        totalM = Minutes.minutesBetween(dateTime1, dateTime2).minutes
                        totalS = Seconds.secondsBetween(dateTime1, dateTime2).seconds
                        totalTime += totalH.div(24).plus(totalM.div(60)).plus(totalS)
                        qtdKwh += 1
                        kWh = kWh?.plus(listPower[i - 1].kW!!)!!
                    }
                }

            } else {
                qtdKwh += 1
                kWh = ((kWh?.plus(listPower[i - 1].kW!!)!!).times(totalTime.div(3600))).div(qtdKwh)
                val dataPoint = DataPoint(dateTime1.dayOfMonth, kWh)
                list.add(Entry(dataPoint.getxValue().toFloat(), dataPoint.getyValue()))
                showChart(list)
                totalKwh = totalKwh?.plus(kWh)
                kWh = 0.0f
                totalTime = 0.0f
                qtdKwh = 0
            }
        }

        val decimalFormat3 = DecimalFormat("#.###")
        val formatStr = decimalFormat3.format(totalKwh).replaceAfter(".", ",")
        binding.consumption.text = formatStr.plus(" kWh")
        val valueRs = totalKwh?.times(0.637)
        val decimalFormat2 = DecimalFormat("#.##")
        val valueStr = "R$ ".plus(decimalFormat2.format(valueRs).replaceAfter(".", ","))
        binding.value.text = valueStr

    }

    private fun showChart(dataVals: ArrayList<Entry>) {
        lineDataSet.values = dataVals
        dataVals.size
        lineDataSet.label = "Consumo em kWh"
        iLineDataSets.clear()
        lineDataSet.color = Color.rgb(245, 191, 27)
        lineDataSet.lineWidth = 2f
        lineDataSet.setCircleColor(Color.rgb(245, 191, 27))
        lineDataSet.circleRadius = 4f
        lineDataSet.fillColor = Color.rgb(245, 191, 27)
        lineDataSet.circleHoleColor = Color.rgb(245, 191, 27)
        lineDataSet.mode = LineDataSet.Mode.LINEAR
        lineDataSet.setDrawValues(true)
        lineDataSet.valueTextSize = 16f
        lineDataSet.valueTextColor = Color.rgb(7, 176, 242)

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
        legend.textColor = Color.rgb(245, 191, 27)
        legend.textSize = 10F
        legend.form = Legend.LegendForm.CIRCLE
        legend.formSize = 5F

        val xAxis = binding.lineChart.xAxis
        val yAxis = binding.lineChart.axisLeft
        val yAxisRight = binding.lineChart.axisRight

        xAxis.setLabelCount(4, true)
        xAxis.axisLineColor = Color.BLACK
        xAxis.textColor = Color.rgb(141, 142, 152)
        xAxis.gridColor = Color.BLACK
        yAxis.axisLineColor = Color.BLACK
        yAxis.textColor = Color.rgb(141, 142, 152)
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

