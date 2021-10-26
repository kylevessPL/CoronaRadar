package pl.piasta.coronaradar.ui.account.view

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentAccountBinding
import pl.piasta.coronaradar.ui.account.viewmodel.AccountViewModel
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.user.view.SignOutDialog
import pl.piasta.coronaradar.ui.user.viewmodel.UserViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.ui.util.observeNull
import pl.piasta.coronaradar.util.TAG

@AndroidEntryPoint
class AccountFragment :
    BaseFragment<FragmentAccountBinding, AccountViewModel>(R.layout.fragment_account) {

    override val viewModel: AccountViewModel by viewModels()
    private val activityViewModel: UserViewModel by activityViewModels()

    override fun updateUI() {
        activityViewModel.firebaseUser.observeNull(
            viewLifecycleOwner,
            { navigateToLoginFragment() })
        viewModel.signOut.observeNotNull(
            viewLifecycleOwner,
            { displaySignOutDialog() })
    }

    private fun navigateToLoginFragment() {
        val action = AccountFragmentDirections.actionAccountFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun displaySignOutDialog() {
        SignOutDialog().show(parentFragmentManager, SignOutDialog::class.TAG)
    }
}