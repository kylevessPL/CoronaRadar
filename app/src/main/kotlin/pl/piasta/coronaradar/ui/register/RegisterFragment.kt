package pl.piasta.coronaradar.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentRegisterBinding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.common.OkDialog
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.TAG
import splitties.resources.str
import splitties.toast.longToast

@AndroidEntryPoint
class RegisterFragment :
    BaseFragment<FragmentRegisterBinding, RegisterViewModel>(R.layout.fragment_register) {

    override val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    private fun updateUI() {
        viewModel.signIn.observeNotNull(viewLifecycleOwner, { navigateToLoginFragment() })
        viewModel.signUpResult.observeNotNull(viewLifecycleOwner, { displaySignUpResult(it) })
        viewModel.verificationResult.observeNotNull(
            viewLifecycleOwner,
            { displayVerificationResult(it) })
    }

    private fun navigateToLoginFragment() {
        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun displaySignUpResult(result: ResultState<FirebaseUser>?) {
        when (result) {
            is ResultState.Success -> viewModel.verify()
            is ResultState.Error -> {
                viewModel.setProgressIndicationVisibility(false)
                when (result.ex) {
                    is FirebaseAuthUserCollisionException -> OkDialog(
                        str(R.string.register_failure),
                        str(R.string.register_failure_email_exists_message)
                    ).show(
                        parentFragmentManager,
                        OkDialog::class.TAG
                    )
                    else -> longToast(R.string.general_failure_message)
                }
            }
            ResultState.Loading -> viewModel.setProgressIndicationVisibility(true)
        }
    }

    private fun displayVerificationResult(result: ResultState<FirebaseUser>?) {
        when (result) {
            is ResultState.Success -> {
                viewModel.setProgressIndicationVisibility(false)
                OkDialog(
                    str(R.string.register_success),
                    str(R.string.register_success_message)
                ) { navigateToLoginFragment() }.show(
                    parentFragmentManager,
                    OkDialog::class.TAG
                )
            }
            is ResultState.Error -> {
                viewModel.setProgressIndicationVisibility(false)
                longToast(R.string.general_failure_message)
            }
            else -> {
            }
        }
    }
}