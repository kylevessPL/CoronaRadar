package pl.piasta.coronaradar.ui.account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentAccountBinding
import pl.piasta.coronaradar.ui.base.BaseFragment

@AndroidEntryPoint
class AccountFragment :
    BaseFragment<FragmentAccountBinding, AccountViewModel>(R.layout.fragment_account) {

    override val viewModel: AccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.displayNameInputLayout.setEndIconOnClickListener {
            viewModel.toggleDisplayName()
        }
        binding.passwordInputLayout.setEndIconOnClickListener {
            viewModel.togglePassword()
        }
    }
}