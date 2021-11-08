package br.com.kwh_monitoring.helpers

import android.graphics.Color
import android.widget.TextView
import br.com.kwh_monitoring.R
import br.com.kwh_monitoring.enum.ChartViewType
import br.com.kwh_monitoring.enum.ChartViewType.*
import br.com.kwh_monitoring.models.Nav
import br.com.kwh_monitoring.models.Portfolio
import br.com.kwh_monitoring.utils.DateTimeUtil
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

import java.util.ArrayList

class ChartViewHelper(private val mChart: LineChart, private val mChartDesc: TextView) {
    private var mPortfolioByDayList: List<Portfolio>? = null
    private var mPortfolioByMonthList: List<Portfolio>? = null
    private var mPortfolioByQuarterList: List<Portfolio>? = null
    fun displayChart(chartViewType: ChartViewType) {

        // display x-axis with date time again
        reformatXValues(mChart, chartViewType)
        var portfolioList: List<Portfolio>? = null
        when (chartViewType) {
            DAILY -> {
                portfolioList = mPortfolioByDayList
                mChartDesc.setText(R.string.text_chart_desc_daily)
            }
            MONTHLY -> {
                portfolioList = mPortfolioByMonthList
                mChartDesc.setText(R.string.text_chart_desc_monthly)
            }
            QUARTERLY -> {
                portfolioList = mPortfolioByQuarterList
                mChartDesc.setText(R.string.text_chart_desc_quarterly)
            }
        }
        if (portfolioList != null) {
            // set data
            mChart.data = getLineData(portfolioList)
            // draw chart
            mChart.invalidate()
        } else {
            showNoDataText()
        }
    }

    private fun reformatXValues(chart: LineChart, chartViewType: ChartViewType) {
        chart.xAxis.setValueFormatter(IAxisValueFormatter { value, axis ->
            when (chartViewType) {
                DAILY -> DateTimeUtil.convertFloatDateToStringDate(value)
                MONTHLY -> DateTimeUtil.convertFloatDateToMonth(value)
                QUARTERLY -> DateTimeUtil.convertFloatDateToQuarter(value)
                else -> ""
            }
        } as ValueFormatter?)
    }

    private fun getLineData(portfolioList: List<Portfolio>?): LineData? {
        if (portfolioList != null) {
            // create new line list
            val lineList = ArrayList<ILineDataSet>()
            // add each line to line list
            for (i in portfolioList.indices) {
                val line = LineDataSet(
                    getEntryList(portfolioList[i].navs),
                    if (i < 3) "Portfolio " + (i + 1) else "Total"
                )
                line.fillAlpha = 110
                line.color = randomColor(i)
                line.setCircleColor(randomColor(i))
                line.lineWidth = 3f
                line.circleRadius = 5f
                line.setDrawCircleHole(false)
                line.valueTextSize = 14f
                line.setDrawFilled(false)
                line.setDrawHighlightIndicators(true)
                lineList.add(line)
            }
            return LineData(lineList)
        }
        return null
    }

    private fun getEntryList(navList: List<Nav>?): ArrayList<Entry>? {
        if (navList != null) {
            val entryList = ArrayList<Entry>()
            for (i in navList.indices) {
                // convert date to timestamp
                val timestamp = DateTimeUtil.convertDateStringToMillis(navList[i].date)
                // add entry to entry list
                entryList.add(Entry(timestamp.toFloat(), navList[i].amount))
            }
            return entryList
        }
        return null
    }

    private fun randomColor(i: Int): Int {
        return if (i == 0) Color.GREEN else if (i == 1) Color.BLUE else if (i == 2) Color.RED else Color.BLACK
    }

    fun showNoDataText() {
        mChart.setNoDataText(mChart.context.getString(R.string.error_load_data))
    }

    var portfolioByDayList: List<Any>?
        get() = mPortfolioByDayList
        set(mPortfolioByDayList) {
            this.mPortfolioByDayList = mPortfolioByDayList as List<Portfolio>?
        }
    var portfolioByMonthList: List<Any>?
        get() = mPortfolioByMonthList
        set(mPortfolioByMonthList) {
            this.mPortfolioByMonthList = mPortfolioByMonthList as List<Portfolio>?
        }
    var portfolioByQuarterList: List<Any>?
        get() = mPortfolioByQuarterList
        set(mPortfolioByQuarterList) {
            this.mPortfolioByQuarterList = mPortfolioByQuarterList as List<Portfolio>?
        }

}