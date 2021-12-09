package pl.piasta.coronaradar.ui.stats.view

import android.graphics.Color.WHITE
import android.graphics.Typeface.DEFAULT_BOLD
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
import com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
import com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.TOP
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS
import com.github.mikephil.charting.utils.ColorTemplate.JOYFUL_COLORS
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.common.Continent
import pl.piasta.coronaradar.databinding.FragmentChart2Binding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsChartViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ifTrue
import splitties.resources.str

@AndroidEntryPoint
class StatsChart2Fragment : BaseFragment<FragmentChart2Binding, StatsChartViewModel>(
    R.string.chart2,
    R.layout.fragment_chart2
) {

    override val viewModel: StatsChartViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun setupView() {
        val chart = binding.chart
        chart.setExtraOffsets(5F, 50F, 5F, 5F)
        chart.setDrawGridBackground(false)
        chart.setDrawBarShadow(false)
        chart.description.isEnabled = false
        chart.legend.apply {
            orientation = VERTICAL
            verticalAlignment = TOP
            horizontalAlignment = CENTER
            textSize = 14F
            textColor = WHITE
            typeface = DEFAULT_BOLD
            yOffset = 10F
        }
        chart.isDoubleTapToZoomEnabled = false
        chart.isHighlightPerTapEnabled = false
        chart.isHighlightFullBarEnabled = true
        chart.isHighlightPerDragEnabled = false
        chart.axisLeft.apply {
            axisMinimum = 0F
            granularity = 1F
            textSize = 13F
            textColor = WHITE
            xOffset = 10F
        }
        chart.axisRight.isEnabled = false
        chart.xAxis.isEnabled = false
    }

    override fun updateUI() {
        viewModel.currentChartDisplayed.observe(viewLifecycleOwner) { position ->
            (position == 1).ifTrue { animateChart() }
        }
        viewModel.statisticsData.observeNotNull(viewLifecycleOwner) { updateChartData(it.continentStatistics.positive) }
    }

    private fun animateChart() = binding.chart.animateY(1400, Easing.EaseInOutQuad)

    private fun updateChartData(data: Map<Continent, Long>) = data.takeIf { it.isNotEmpty() }?.let {
        val dataSets = arrayListOf<IBarDataSet>()
        it.toList().forEachIndexed { index, (continent, value) ->
            val entries = listOf(BarEntry(index.toFloat(), value.toFloat()))
            val dataSet = BarDataSet(entries, str(continent.label)).apply {
                setDrawIcons(false)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float) = value.toInt().toString()
                }
                valueTextSize = 12F
                valueTextColor = WHITE
                color = COLORFUL_COLORS.plus(JOYFUL_COLORS)[index]
            }
            dataSets.add(dataSet)
        }
        binding.chart.apply {
            setData(BarData(dataSets))
            notifyDataSetChanged()
            invalidate()
        }
    }
}