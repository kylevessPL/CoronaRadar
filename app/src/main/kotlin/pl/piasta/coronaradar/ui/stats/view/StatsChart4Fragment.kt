package pl.piasta.coronaradar.ui.stats.view

import android.graphics.Color.WHITE
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.common.AgeRange
import pl.piasta.coronaradar.data.common.ResultLabel
import pl.piasta.coronaradar.data.survey.model.AgeStatistics
import pl.piasta.coronaradar.databinding.FragmentChart4Binding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsChartViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ifTrue
import splitties.resources.str
import java.text.DecimalFormat

@AndroidEntryPoint
class StatsChart4Fragment : BaseFragment<FragmentChart4Binding, StatsChartViewModel>(
    R.string.chart,
    R.layout.fragment_chart4
) {

    override val viewModel: StatsChartViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun updateUI() {
        viewModel.currentChartDisplayed.observe(viewLifecycleOwner) { position ->
            (position == 3).ifTrue { animateChart() }
        }
        viewModel.statisticsData.observeNotNull(viewLifecycleOwner) { updateChartData(it.ageStatistics) }
    }

    private fun animateChart() = binding.chart.animateY(1400, Easing.EaseInOutQuad)

    private fun updateChartData(data: AgeStatistics) =
        data.takeIf { it.positive.isNotEmpty() || it.negative.isNotEmpty() }?.let {
            val dataSets = listOf(data.negative, data.positive).mapIndexed { index, map ->
                val entries =
                    enumValues<AgeRange>().map { RadarEntry(map.getOrDefault(it, 0).toFloat()) }
                RadarDataSet(entries, str(enumValues<ResultLabel>()[index].label)).apply {
                    color = ColorTemplate.MATERIAL_COLORS[index]
                    fillColor = ColorTemplate.MATERIAL_COLORS[index]
                    setDrawFilled(true)
                    fillAlpha = 180
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float) =
                            DecimalFormat("###,###,##0").format(value)
                    }
                    valueTextSize = 10F
                    valueTextColor = WHITE
                    lineWidth = 2F
                    isDrawHighlightCircleEnabled = true
                    setDrawHighlightIndicators(false)
                }
            }
            binding.chart.apply {
                setData(RadarData(dataSets))
                notifyDataSetChanged()
                invalidate()
            }
        }
}