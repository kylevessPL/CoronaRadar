package pl.piasta.coronaradar.ui.radar.view

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentRadarBinding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.radar.viewmodel.RadarViewModel

@AndroidEntryPoint
class RadarFragment : BaseFragment<FragmentRadarBinding, RadarViewModel>(R.layout.fragment_radar) {

    override val viewModel: RadarViewModel by viewModels()

    override fun updateUI() {
        TODO("Not yet implemented")
    }
}