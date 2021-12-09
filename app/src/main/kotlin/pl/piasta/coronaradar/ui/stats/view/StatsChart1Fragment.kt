package pl.piasta.coronaradar.ui.stats.view

import android.graphics.Color.WHITE
import android.graphics.Typeface.DEFAULT_BOLD
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.animation.Easing.EaseInOutQuad
import com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE
import com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
import com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
import com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.TOP
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.common.Gender
import pl.piasta.coronaradar.databinding.FragmentChart1Binding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsChartViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.EMPTY
import pl.piasta.coronaradar.util.ifTrue
import splitties.resources.color
import splitties.resources.str
import java.text.DecimalFormat

@AndroidEntryPoint
class StatsChart1Fragment :
    BaseFragment<FragmentChart1Binding, StatsChartViewModel>(
        R.string.chart1,
        R.layout.fragment_chart1
    ) {

    override val viewModel: StatsChartViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun setupView() {
        val chart = binding.chart
        chart.setUsePercentValues(true)
        chart.setExtraOffsets(5F, 20F, 5F, 5F)
        chart.description.isEnabled = false
        chart.centerText = str(R.string.gender)
        chart.setCenterTextSize(18F)
        chart.setCenterTextTypeface(DEFAULT_BOLD)
        chart.isRotationEnabled = false
        chart.legend.apply {
            orientation = VERTICAL
            verticalAlignment = TOP
            horizontalAlignment = CENTER
            form = CIRCLE
            textSize = 14F
            textColor = WHITE
            typeface = DEFAULT_BOLD
            yOffset = 10F
        }
        chart.setEntryLabelTextSize(13f)
    }

    override fun updateUI() {
        viewModel.currentChartDisplayed.observe(viewLifecycleOwner) { position ->
            (position == 0).ifTrue { animateChart() }
        }
        viewModel.statisticsData.observeNotNull(viewLifecycleOwner) { updateChartData(it.genderStatistics.positive) }
    }

    private fun animateChart() = binding.chart.animateY(1400, EaseInOutQuad)

    private fun updateChartData(data: Map<Gender, Long>) = data.takeIf { it.isNotEmpty() }?.let {
        val entries = it.map { value -> PieEntry(value.value.toFloat(), str(value.key.label)) }
        val dataSet = PieDataSet(entries, String.EMPTY).apply {
            setDrawIcons(false)
            valueFormatter = PercentFormatter().apply { mFormat = DecimalFormat("###,###,##0 '%'") }
            valueTextSize = 12F
            valueTextColor = WHITE
            sliceSpace = 3F
            colors = listOf(color(R.color.google_dark_blue), color(R.color.google_red))
        }
        binding.chart.apply {
            setData(PieData(dataSet))
            notifyDataSetChanged()
            invalidate()
        }
    }
}