package pl.piasta.coronaradar.ui.stats.view

import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentChart3Binding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsViewModel

@AndroidEntryPoint
class StatsChart3Fragment :
    BaseFragment<FragmentChart3Binding, StatsViewModel>(R.string.chart3, R.layout.fragment_chart3)