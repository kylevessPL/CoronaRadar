package pl.piasta.coronaradar.ui.history.view

import androidx.fragment.app.viewModels
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentHistoryBinding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.common.adapter.FooterLoadStateAdapter
import pl.piasta.coronaradar.ui.history.adapter.HistoryAdapter
import pl.piasta.coronaradar.ui.history.viewmodel.HistoryViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.ui.util.setGoogleSchemeColors
import pl.piasta.coronaradar.util.ifTrue
import splitties.toast.toast

@AndroidEntryPoint
class HistoryFragment :
    BaseFragment<FragmentHistoryBinding, HistoryViewModel>(R.layout.fragment_history) {

    private val adapter = HistoryAdapter()

    override val viewModel: HistoryViewModel by viewModels()

    override fun setupView() {
        setupSwipeRefresh()
        setupAdapter()
    }

    override fun updateUI() {
        viewModel.historyData.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }
        viewModel.refreshData.observeNotNull(viewLifecycleOwner) {
            adapter.refresh()
        }
    }

    private fun setupSwipeRefresh() = binding.historySwipeRefresh.setGoogleSchemeColors()

    private fun setupAdapter() {
        binding.historyRecyclerView.adapter = adapter.apply {
            addLoadStateListener {
                viewModel.setDataRefreshing(it.refresh is Loading)
                (it.refresh is Error).ifTrue { toast(R.string.fetch_data_failure_message) }
            }
        }.withLoadStateFooter(FooterLoadStateAdapter(adapter::retry))
    }
}