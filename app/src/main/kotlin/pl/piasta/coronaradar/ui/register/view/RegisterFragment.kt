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
import pl.piasta.coronaradar.ui.common.view.OkDialogFragment
import pl.piasta.coronaradar.ui.register.viewmodel.RegisterViewModel
import pl.piasta.coronaradar.ui.user.viewmodel.UserViewModel
import pl.piasta.coronaradar.ui.util.newFragmentInstance
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.ResultState.*
import pl.piasta.coronaradar.util.TAG
import pl.piasta.coronaradar.util.ifTrue
import splitties.toast.longToast

@AndroidEntryPoint
class RegisterFragment :
    BaseFragment<FragmentRegisterBinding, RegisterViewModel>(
        R.string.signup,
        R.layout.fragment_register
    ) {

    override val viewModel: RegisterViewModel by viewModels()
    private val activityViewModel: UserViewModel by activityViewModels()

    override fun updateUI() {
        activityViewModel.firebaseUser.observeNotNull(viewLifecycleOwner) {
            navigateToAccountFragment()
        }
        viewModel.signIn.observeNotNull(viewLifecycleOwner) {
            navigateToLoginFragment()
        }
        viewModel.signUpResult.observeNotNull(viewLifecycleOwner) {
            displaySignUpResult(it)
        }
        activityViewModel.verificationEmailResult.observeNotNull(viewLifecycleOwner) {
            displayVerificationEmailResult(it)
        }
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
            is Success -> {
                viewModel.setProgressIndicatorVisibility(false)
                activityViewModel.sendVerificationEmail()
            }
            is Error -> {
                when (result.ex) {
                    is FirebaseAuthUserCollisionException -> newFragmentInstance<OkDialogFragment>(
                        "data" to OkDialogData(
                            R.string.register_failure,
                            R.string.register_failure_email_exists_message
                        )
                    ).show(
                        parentFragmentManager,
                        OkDialogFragment::class.TAG
                    )
                    else -> longToast(R.string.general_failure_message)
                }
            }
            Loading -> viewModel.setProgressIndicatorVisibility(true)
        }
    }

    private fun displayVerificationEmailResult(result: ResultState<FirebaseUser>) =
        (result is Success).ifTrue {
            newFragmentInstance<OkDialogFragment>(
                "data" to OkDialogData(
                    R.string.register_success,
                    R.string.register_success_message,
                    { navigateToLoginFragment() })
            ).show(
                parentFragmentManager,
                OkDialogFragment::class.TAG
            )
        }
}