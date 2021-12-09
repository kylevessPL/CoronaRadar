package pl.piasta.coronaradar.ui.stats.view

import android.graphics.Color
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentChart3Binding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsChartViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.EMPTY
import pl.piasta.coronaradar.util.ifTrue
import java.time.YearMonth

@AndroidEntryPoint
class StatsChart3Fragment : BaseFragment<FragmentChart3Binding, StatsChartViewModel>(
    R.string.chart3,
    R.layout.fragment_chart3
) {

    override val viewModel: StatsChartViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun setupView() {
        val chart = binding.chart
        chart.setExtraOffsets(5F, 30F, 5F, 5F)
        chart.description.isEnabled = false
        chart.setTouchEnabled(false)
        chart.isDragEnabled = false
        val x = chart.xAxis
        x.setDrawAxisLine(false)
        x.setDrawGridLines(false)
        x.textColor = Color.WHITE
        x.textSize = 13F
        x.yOffset = 20F
        x.granularity = 1F
        x.setAvoidFirstLastClipping(true)
        val y = chart.axisLeft
        y.setLabelCount(6, false)
        y.textColor = Color.WHITE
        y.textSize = 13F
        y.axisMinimum = 0F
        y.granularity = 1F
        y.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float) = value.toInt().toString()
        }
        y.axisLineColor = Color.WHITE
        y.xOffset = 10F
        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false
    }

    override fun updateUI() {
        viewModel.currentChartDisplayed.observe(viewLifecycleOwner) { position ->
            (position == 2).ifTrue { animateChart() }
        }
        viewModel.statisticsData.observeNotNull(viewLifecycleOwner) { updateChartData(it.dateStatistics.positive) }
    }

    private fun animateChart() = binding.chart.animateY(1400, Easing.EaseInOutQuad)

    private fun updateChartData(data: Map<YearMonth, Long>) = data.takeIf { it.isNotEmpty() }?.let {
        val map = it.toList().sortedByDescending { value -> value.first }.take(4).asReversed()
        val values = map.mapIndexed { index, (_, value) -> Entry(index.toFloat(), value.toFloat()) }
        val dataSet = LineDataSet(values, String.EMPTY).apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            setDrawCircles(false)
            setDrawValues(false)
            lineWidth = 1.8f
            setCircleColor(Color.WHITE)
            color = Color.WHITE
            fillColor = Color.WHITE
            fillAlpha = 100
        }
        binding.chart.apply {
            xAxis.valueFormatter = object : ValueFormatter() {
                val labels = map.map { value -> value.first.toString() }
                override fun getFormattedValue(value: Float) = labels[value.toInt() % labels.size]
            }
            setData(LineData(dataSet))
            notifyDataSetChanged()
            invalidate()
        }
    }
}