package pl.piasta.coronaradar.ui.stats

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentStatsBinding
import pl.piasta.coronaradar.ui.base.BaseFragment

@AndroidEntryPoint
class StatsFragment : BaseFragment<FragmentStatsBinding, StatsViewModel>(R.layout.fragment_stats) {

    override val viewModel: StatsViewModel by viewModels()
}