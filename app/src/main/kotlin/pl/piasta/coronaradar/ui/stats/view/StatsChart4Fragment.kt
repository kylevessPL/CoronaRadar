package pl.piasta.coronaradar.ui.stats.view

import android.graphics.Color.LTGRAY
import android.graphics.Color.WHITE
import android.graphics.Typeface
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
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
    R.string.chart4,
    R.layout.fragment_chart4
) {

    override val viewModel: StatsChartViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun setupView() {
        val chart = binding.chart
        chart.setExtraOffsets(5F, 5F, 5F, 5F)
        chart.description.isEnabled = false
        chart.webColor = LTGRAY
        chart.webColorInner = LTGRAY
        chart.webLineWidthInner = 1F
        chart.webAlpha = 100
        chart.isRotationEnabled = false
        val xAxis = chart.xAxis
        xAxis.textSize = 13F
        xAxis.textColor = WHITE
        xAxis.valueFormatter = object : ValueFormatter() {
            val labels = enumValues<AgeRange>().map { str(it.label) }
            override fun getFormattedValue(value: Float) = labels[value.toInt() % labels.size]
        }
        val yAxis = chart.yAxis
        yAxis.setLabelCount(5, false)
        yAxis.axisMinimum = 0F
        yAxis.setDrawLabels(false)
        val l = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.textColor = WHITE
        l.textSize = 14F
        l.typeface = Typeface.DEFAULT_BOLD
        l.yOffset = 10F
    }

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