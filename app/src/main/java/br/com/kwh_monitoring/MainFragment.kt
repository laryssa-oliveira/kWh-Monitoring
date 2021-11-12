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

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val database = Firebase.database
    private val referenceStatus = database.getReference("LED_STATUS")
    private var databaseRef: DatabaseReference = database.reference.child("chartTable")
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

        val list = ArrayList<Entry>()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                for (snapshot in dataSnapshot.children) {
                    val consumption: Consumption? = snapshot.getValue(Consumption::class.java)
                    val kWh = consumption?.getkWh()
                    val day = consumption?.getDay()
                    val dataPoint = DataPoint(day,kWh)
                    list.add(Entry(dataPoint.getxValue().toFloat(), dataPoint.getyValue()))
                    showChart(list)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {  }
        })

    }

    private fun showChart(dataVals: ArrayList<Entry>) {
        lineDataSet.values = dataVals
        lineDataSet.label = "Consumo em kWh"
        iLineDataSets.clear()
        lineDataSet.color = Color.rgb(61, 224, 242)
        lineDataSet.lineWidth = 2.5f
        lineDataSet.setCircleColor(Color.rgb(61, 224, 242))
        lineDataSet.circleRadius = 5f
        lineDataSet.fillColor = Color.rgb(61, 224, 242)
        lineDataSet.circleHoleColor = Color.rgb(61, 224, 242)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
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

