package pl.piasta.coronaradar.ui.stats.view

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.StatsChartDialogBinding
import pl.piasta.coronaradar.ui.base.BaseDialogFragment
import pl.piasta.coronaradar.ui.common.adapter.ViewPagerAdapter
import pl.piasta.coronaradar.ui.common.adapter.ZoomOutPageTransformer
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsChartViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull

@AndroidEntryPoint
class StatsChartDialogFragment :
    BaseDialogFragment<StatsChartDialogBinding, StatsChartViewModel>(R.layout.stats_chart_dialog) {

    companion object;

    override val dialogTheme = R.style.Theme_CoronaRadar_Dialog_FullScreen

    override val viewModel: StatsChartViewModel by viewModels()

    override fun setupView() {
        dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
        setupViewPager()
    }

    override fun updateUI() {
        viewModel.dialogDismiss.observeNotNull(viewLifecycleOwner) { dismiss() }
    }

    private fun setupViewPager() {
        with(binding.viewPager) {
            adapter = ViewPagerAdapter(
                listOf(
                    StatsChart1Fragment(),
                    StatsChart2Fragment(),
                    StatsChart3Fragment(),
                    StatsChart4Fragment()
                ),
                this@StatsChartDialogFragment
            )
            setPageTransformer { page, position ->
                ZoomOutPageTransformer().transformPage(page, position)
            }
            binding.statsChartDotsIndicator.setViewPager2(this)
        }
    }
}