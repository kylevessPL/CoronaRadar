package pl.piasta.coronaradar.ui.history.view

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentHistoryBinding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.history.viewmodel.HistoryViewModel

@AndroidEntryPoint
class HistoryFragment :
    BaseFragment<FragmentHistoryBinding, HistoryViewModel>(R.layout.fragment_history) {

    override val viewModel: HistoryViewModel by viewModels()

    override fun updateUI() {
        //TODO("Not yet implemented")
    }
}