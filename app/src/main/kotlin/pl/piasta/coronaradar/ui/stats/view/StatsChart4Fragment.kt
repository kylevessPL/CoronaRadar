package pl.piasta.coronaradar.ui.stats.view

import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentChart4Binding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsViewModel

@AndroidEntryPoint
class StatsChart4Fragment :
    BaseFragment<FragmentChart4Binding, StatsViewModel>(R.string.chart4, R.layout.fragment_chart4)