package pl.piasta.coronaradar.ui.stats.view

import android.view.View
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.paging.LoadState.Loading
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.databinding.FragmentStatsBinding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.common.adapter.FooterLoadStateAdapter
import pl.piasta.coronaradar.ui.common.adapter.RecyclerViewClickListener
import pl.piasta.coronaradar.ui.stats.adapter.StatsAdapter
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.ui.util.setGoogleSchemeColors
import pl.piasta.coronaradar.util.ifTrue
import splitties.toast.toast

@AndroidEntryPoint
class StatsFragment : BaseFragment<FragmentStatsBinding, StatsViewModel>(R.layout.fragment_stats) {

    private val adapter = StatsAdapter(object : RecyclerViewClickListener<Survey> {

        override fun onRecyclerViewItemClick(view: View, item: Survey) {
            viewModel.handleItemClicked(item)
        }
    })

    override val viewModel: StatsViewModel by viewModels()

    override fun setupView() {
        setupSwipeRefresh()
        setupAdapter()
    }

    override fun updateUI() {
        viewModel.surveysData.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }
        viewModel.refreshData.observeNotNull(viewLifecycleOwner) {
            adapter.refresh()
        }
    }

    private fun setupSwipeRefresh() = binding.statsSwipeRefresh.setGoogleSchemeColors()

    private fun setupAdapter() {
        binding.statsRecyclerView.adapter = adapter.apply {
            addLoadStateListener {
                viewModel.setDataRefreshing(it.refresh is Loading)
                (it.refresh is LoadState.Error).ifTrue { toast(R.string.fetch_data_failure_message) }
            }
        }.withLoadStateFooter(FooterLoadStateAdapter(adapter::retry))
    }
}