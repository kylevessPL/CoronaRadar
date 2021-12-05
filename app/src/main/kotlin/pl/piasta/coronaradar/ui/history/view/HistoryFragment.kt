package pl.piasta.coronaradar.ui.history.view

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.paging.LoadState.*
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentHistoryBinding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.common.adapter.FooterLoadStateAdapter
import pl.piasta.coronaradar.ui.history.adapter.HistoryAdapter
import pl.piasta.coronaradar.ui.history.viewmodel.HistoryViewModel
import pl.piasta.coronaradar.ui.main.viewmodel.MainViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.ui.util.setGoogleSchemeColors
import pl.piasta.coronaradar.util.ifTrue
import splitties.toast.toast

@AndroidEntryPoint
class HistoryFragment :
    BaseFragment<FragmentHistoryBinding, HistoryViewModel>(
        R.string.my_history,
        R.layout.fragment_history
    ) {

    @Suppress("NOTIFYDATASETCHANGED")
    private val timeZoneObserver by lazy {
        Observer<Intent> { adapter.notifyDataSetChanged() }
    }

    private val adapter = HistoryAdapter()

    override val viewModel: HistoryViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    override fun setupView() {
        setupSwipeRefresh()
        setupAdapter()
    }

    override fun updateUI() {
        activityViewModel.timeZone.observeForever(timeZoneObserver)
        viewModel.historyData.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }
        viewModel.refreshData.observeNotNull(viewLifecycleOwner) {
            adapter.refresh()
        }
    }

    override fun cleanup() {
        activityViewModel.timeZone.removeObserver(timeZoneObserver)
    }

    private fun setupSwipeRefresh() = binding.historySwipeRefresh.setGoogleSchemeColors()

    private fun setupAdapter() {
        binding.contentHistory.recyclerView.adapter = adapter.apply {
            addLoadStateListener {
                viewModel.setDataRefreshing(it.refresh is Loading)
                viewModel.setEmptyPlaceholderVisibility(it.refresh is NotLoading && it.append.endOfPaginationReached && adapter.itemCount == 0)
                (it.refresh is Error).ifTrue { toast(R.string.fetch_data_failure_message) }
            }
        }.withLoadStateFooter(FooterLoadStateAdapter(adapter::retry))
    }
}