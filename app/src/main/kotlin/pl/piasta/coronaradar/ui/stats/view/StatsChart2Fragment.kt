package pl.piasta.coronaradar.ui.stats.view

import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentChart2Binding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsViewModel

@AndroidEntryPoint
class StatsChart2Fragment :
    BaseFragment<FragmentChart2Binding, StatsViewModel>(R.string.chart2, R.layout.fragment_chart2)