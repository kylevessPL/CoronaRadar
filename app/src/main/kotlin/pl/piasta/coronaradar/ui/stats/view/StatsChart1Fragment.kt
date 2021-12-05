package pl.piasta.coronaradar.ui.stats.view

import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentChart1Binding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsViewModel

@AndroidEntryPoint
class StatsChart1Fragment :
    BaseFragment<FragmentChart1Binding, StatsViewModel>(R.string.chart1, R.layout.fragment_chart1) {
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding.chart1.setCenterTextSize()
//        return super.onCreateView(inflater, container, savedInstanceState)
//    }
}