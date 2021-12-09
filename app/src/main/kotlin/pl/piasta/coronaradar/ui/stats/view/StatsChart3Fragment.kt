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
    R.string.chart,
    R.layout.fragment_chart3
) {

    override val viewModel: StatsChartViewModel by viewModels(ownerProducer = { requireParentFragment() })

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