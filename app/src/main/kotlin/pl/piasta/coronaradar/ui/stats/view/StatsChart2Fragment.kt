package pl.piasta.coronaradar.ui.stats.view

import android.graphics.Color.WHITE
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.animation.Easing
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
    R.string.chart,
    R.layout.fragment_chart2
) {

    override val viewModel: StatsChartViewModel by viewModels(ownerProducer = { requireParentFragment() })

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