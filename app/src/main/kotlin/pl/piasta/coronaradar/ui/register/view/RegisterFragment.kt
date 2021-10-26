package pl.piasta.coronaradar.ui.register.view

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentRegisterBinding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.common.model.OkDialogData
import pl.piasta.coronaradar.ui.common.view.OkDialog
import pl.piasta.coronaradar.ui.register.viewmodel.RegisterViewModel
import pl.piasta.coronaradar.ui.user.viewmodel.UserViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.ResultState.Error
import pl.piasta.coronaradar.util.ResultState.Loading
import pl.piasta.coronaradar.util.ResultState.Success
import pl.piasta.coronaradar.util.TAG
import splitties.resources.str
import splitties.toast.longToast

@AndroidEntryPoint
class RegisterFragment :
    BaseFragment<FragmentRegisterBinding, RegisterViewModel>(R.layout.fragment_register) {

    override val viewModel: RegisterViewModel by viewModels()
    private val activityViewModel: UserViewModel by activityViewModels()

    override fun updateUI() {
        activityViewModel.firebaseUser.observeNotNull(
            viewLifecycleOwner,
            { navigateToAccountFragment() })
        viewModel.signIn.observeNotNull(viewLifecycleOwner, { navigateToLoginFragment() })
        viewModel.signUpResult.observeNotNull(viewLifecycleOwner, { displaySignUpResult(it) })
        viewModel.verificationEmailResult.observeNotNull(
            viewLifecycleOwner,
            { displayVerificationEmailResult(it) })
    }

    private fun navigateToLoginFragment() {
        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun navigateToAccountFragment() {
        val action = RegisterFragmentDirections.actionRegisterFragmentToAccountFragment()
        findNavController().navigate(action)
    }

    private fun displaySignUpResult(result: ResultState<FirebaseUser>) {
        when (result) {
            is Success -> viewModel.sendVerificationEmail()
            is Error -> {
                viewModel.setProgressIndicationVisibility(false)
                when (result.ex) {
                    is FirebaseAuthUserCollisionException -> OkDialog.newInstance(
                        OkDialogData(
                            str(R.string.register_failure),
                            str(R.string.register_failure_email_exists_message)
                        )
                    ).show(
                        parentFragmentManager,
                        OkDialog::class.TAG
                    )
                    else -> longToast(R.string.general_failure_message)
                }
            }
            Loading -> viewModel.setProgressIndicationVisibility(true)
        }
    }

    private fun displayVerificationEmailResult(result: ResultState<FirebaseUser>) {
        when (result) {
            is Success -> {
                viewModel.setProgressIndicationVisibility(false)
                OkDialog.newInstance(OkDialogData(
                    str(R.string.register_success),
                    str(R.string.register_success_message)
                ) { navigateToLoginFragment() }).show(
                    parentFragmentManager,
                    OkDialog::class.TAG
                )
            }
            is Error -> {
                viewModel.setProgressIndicationVisibility(false)
                longToast(R.string.general_failure_message)
            }
            else -> {
            }
        }
    }
}