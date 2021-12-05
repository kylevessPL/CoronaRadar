package pl.piasta.coronaradar.ui.stats.view

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import androidx.paging.LoadState.Loading
import androidx.paging.LoadState.NotLoading
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.databinding.FragmentStatsBinding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.common.adapter.FooterLoadStateAdapter
import pl.piasta.coronaradar.ui.common.adapter.RecyclerViewClickListener
import pl.piasta.coronaradar.ui.main.viewmodel.MainViewModel
import pl.piasta.coronaradar.ui.stats.adapter.StatsAdapter
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsViewModel
import pl.piasta.coronaradar.ui.util.newFragmentInstance
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.ui.util.setGoogleSchemeColors
import pl.piasta.coronaradar.util.TAG
import pl.piasta.coronaradar.util.ifTrue
import splitties.toast.toast

@AndroidEntryPoint
class StatsFragment :
    BaseFragment<FragmentStatsBinding, StatsViewModel>(R.string.stats, R.layout.fragment_stats) {

    @Suppress("NOTIFYDATASETCHANGED")
    private val timeZoneObserver by lazy {
        Observer<Intent> { adapter.notifyDataSetChanged() }
    }

    private val adapter = StatsAdapter(object : RecyclerViewClickListener<Survey> {

        override fun onRecyclerViewItemClick(view: View, item: Survey) {
            viewModel.displaySurveyDialogEvent(item)
        }
    })

    override val viewModel: StatsViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_stats_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        (item.itemId == R.id.action_chart).ifTrue {
            StatsChartDialogFragment().show(childFragmentManager, StatsChartDialogFragment.TAG)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setupView() {
        setHasOptionsMenu(true)
        setupSwipeRefresh()
        setupAdapter()
    }

    override fun updateUI() {
        activityViewModel.timeZone.observeForever(timeZoneObserver)
        viewModel.displaySurveyDialog.observeNotNull(viewLifecycleOwner) {
            displaySurveyDialog(it)
        }
        viewModel.surveysData.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }
        viewModel.refreshData.observeNotNull(viewLifecycleOwner) {
            adapter.refresh()
        }
    }

    override fun cleanup() {
        activityViewModel.timeZone.removeObserver(timeZoneObserver)
    }

    private fun displaySurveyDialog(survey: Survey) {
        newFragmentInstance<SurveyDetailsDialogFragment>(SurveyDetailsDialogFragment.DATA to survey).show(
            childFragmentManager,
            SurveyDetailsDialogFragment.TAG
        )
    }

    private fun setupSwipeRefresh() = binding.statsSwipeRefresh.setGoogleSchemeColors()

    private fun setupAdapter() {
        binding.contentStats.recyclerView.adapter = adapter.apply {
            addLoadStateListener {
                viewModel.setDataRefreshing(it.refresh is Loading)
                viewModel.setEmptyPlaceholderVisibility(it.refresh is NotLoading && it.append.endOfPaginationReached && adapter.itemCount == 0)
                (it.refresh is LoadState.Error).ifTrue { toast(R.string.fetch_data_failure_message) }
            }
        }.withLoadStateFooter(FooterLoadStateAdapter(adapter::retry))
    }
}