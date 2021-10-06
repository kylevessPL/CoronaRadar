package pl.piasta.coronaradar.ui.history

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentHistoryBinding
import pl.piasta.coronaradar.ui.base.BaseFragment

@AndroidEntryPoint
class HistoryFragment :
    BaseFragment<FragmentHistoryBinding, HistoryViewModel>(R.layout.fragment_history) {

    override val viewModel: HistoryViewModel by viewModels()
}