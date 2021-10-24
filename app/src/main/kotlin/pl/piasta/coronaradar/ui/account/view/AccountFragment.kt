package pl.piasta.coronaradar.ui.account.view

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentAccountBinding
import pl.piasta.coronaradar.ui.account.viewmodel.AccountViewModel
import pl.piasta.coronaradar.ui.base.BaseFragment

@AndroidEntryPoint
class AccountFragment :
    BaseFragment<FragmentAccountBinding, AccountViewModel>(R.layout.fragment_account) {

    override val viewModel: AccountViewModel by viewModels()

    override fun updateUI() {
        TODO("Not yet implemented")
    }
}