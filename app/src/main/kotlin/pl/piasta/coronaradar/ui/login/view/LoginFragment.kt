package pl.piasta.coronaradar.ui.login.view

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.auth.exception.EmailNotVerifiedException
import pl.piasta.coronaradar.databinding.FragmentLoginBinding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.common.model.OkDialogData
import pl.piasta.coronaradar.ui.common.view.OkDialogFragment
import pl.piasta.coronaradar.ui.login.viewmodel.LoginViewModel
import pl.piasta.coronaradar.ui.user.view.PasswordResetEmailDialogFragment
import pl.piasta.coronaradar.ui.user.viewmodel.UserViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.ResultState.*
import pl.piasta.coronaradar.util.TAG
import pl.piasta.coronaradar.util.ifTrue
import splitties.toast.longToast
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(R.layout.fragment_login) {

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var facebookLoginManager: LoginManager

    private val googleLoginLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            lifecycleScope.launchWhenStarted {
                withContext(Dispatchers.IO) {
                    viewModel.signInWithGoogle(task)
                }
            }
        }

    override val title = R.string.signin

    override val viewModel: LoginViewModel by viewModels()
    private val activityViewModel: UserViewModel by activityViewModels()

    override fun updateUI() {
        activityViewModel.firebaseUser.observeNotNull(viewLifecycleOwner) {
            navigateToAccountFragment()
        }
        viewModel.signUp.observeNotNull(viewLifecycleOwner) {
            navigateToRegisterFragment()
        }
        viewModel.signInWithGoogle.observeNotNull(viewLifecycleOwner) {
            launchGoogleSignInIntent()
        }
        viewModel.signInWithFacebook.observeNotNull(viewLifecycleOwner) {
            launchFacebookSignInIntent()
        }
        activityViewModel.verifyEmailResult.observeNotNull(viewLifecycleOwner) {
            displayVerifyEmailResult(it)
        }
        viewModel.resetPassword.observeNotNull(viewLifecycleOwner) {
            displayPasswordResetEmailDialog()
        }
        viewModel.signInResult.observeNotNull(viewLifecycleOwner) {
            displaySignInResult(it)
        }
    }

    private fun displayPasswordResetEmailDialog() {
        PasswordResetEmailDialogFragment().show(
            parentFragmentManager,
            PasswordResetEmailDialogFragment::class.TAG
        )
    }

    private fun launchGoogleSignInIntent() =
        googleLoginLauncher.launch(googleSignInClient.signInIntent)

    private fun launchFacebookSignInIntent() {
        val callbackManager = CallbackManager.Factory.create()
        lifecycleScope.launchWhenStarted {
            withContext(Dispatchers.IO) {
                viewModel.signInWithFacebook(callbackManager)
            }
        }
        facebookLoginManager.logInWithReadPermissions(
            this,
            callbackManager,
            setOf("email", "public_profile")
        )
    }

    private fun navigateToRegisterFragment() {
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        findNavController().navigate(action)
    }

    private fun navigateToAccountFragment() {
        val action = LoginFragmentDirections.actionLoginFragmentToAccountFragment()
        findNavController().navigate(action)
    }

    private fun displayVerifyEmailResult(result: ResultState<Nothing>) = when (result) {
        is Success -> viewModel.setProgressIndicationVisibility(false)
        is Error -> {
            viewModel.setProgressIndicationVisibility(false)
            longToast(R.string.general_failure_message)
        }
        Loading -> viewModel.setProgressIndicationVisibility(true)
    }

    private fun displaySignInResult(result: ResultState<FirebaseUser>) {
        when (result) {
            is Success -> viewModel.setProgressIndicationVisibility(false)
            is Error -> {
                viewModel.setProgressIndicationVisibility(false)
                when (val ex = result.ex) {
                    is FirebaseAuthException -> {
                        val messageId = when (ex is FirebaseAuthUserCollisionException) {
                            true -> R.string.login_failure_other_provider_message
                            false -> R.string.login_failure_bad_credentials_message
                        }
                        OkDialogFragment.newInstance(
                            OkDialogData(R.string.login_failure, messageId)
                        ).show(
                            parentFragmentManager,
                            OkDialogFragment::class.TAG
                        )
                    }
                    is EmailNotVerifiedException -> OkDialogFragment.newInstance(
                        OkDialogData(
                            R.string.login_failure,
                            R.string.verification_email_message,
                            { activityViewModel.sendVerificationEmail() },
                            R.string.resend_verification_email,
                            true
                        )
                    ).show(
                        parentFragmentManager,
                        OkDialogFragment::class.TAG
                    )
                    else -> !(ex is ApiException && ex.status.isCanceled).ifTrue { longToast(R.string.general_failure_message) }!!
                }
            }
            Loading -> viewModel.setProgressIndicationVisibility(true)
        }
    }
}