package pl.piasta.coronaradar.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.auth.exception.EmailNotVerifiedException
import pl.piasta.coronaradar.databinding.FragmentLoginBinding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.common.OkDialog
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.ResultState.Error
import pl.piasta.coronaradar.util.ResultState.Loading
import pl.piasta.coronaradar.util.ResultState.Success
import pl.piasta.coronaradar.util.TAG
import splitties.resources.str
import splitties.toast.longToast
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(R.layout.fragment_login) {

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var facebookLoginManager: LoginManager

    private val googleLoginLauncher by lazy {
        registerForActivityResult(StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            lifecycleScope.launchWhenStarted {
                withContext(Dispatchers.IO) {
                    viewModel.signInWithGoogle(task)
                }
            }
        }
    }

    override val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    private fun updateUI() {
        viewModel.signUp.observeNotNull(viewLifecycleOwner, { navigateToRegisterFragment() })
        viewModel.signInWithGoogle.observeNotNull(
            viewLifecycleOwner,
            { launchGoogleSignInIntent() })
        viewModel.signInWithFacebook.observeNotNull(
            viewLifecycleOwner,
            { launchFacebookSignInIntent() })
        viewModel.resetPassword.observeNotNull(viewLifecycleOwner, { displayPasswordResetDialog() })
        viewModel.signInResult.observeNotNull(viewLifecycleOwner, { displaySignInResult(it) })
    }

    private fun displayPasswordResetDialog() {
        PasswordResetDialog().show(parentFragmentManager, PasswordResetDialog::class.TAG)
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
            this@LoginFragment,
            callbackManager,
            setOf("email", "public_profile")
        )
    }

    private fun navigateToRegisterFragment() {
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        findNavController().navigate(action)
    }

    private fun displaySignInResult(result: ResultState<FirebaseUser>?) {
        when (result) {
            is Success -> {
                viewModel.setProgressIndicationVisibility(false)
            }
            is Error -> {
                viewModel.setProgressIndicationVisibility(false)
                when (result.ex) {
                    is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> OkDialog(
                        str(R.string.login_failure),
                        str(R.string.login_failure_message)
                    ).show(
                        parentFragmentManager,
                        OkDialog::class.TAG
                    )
                    is EmailNotVerifiedException -> VerificationDialog().show(
                        parentFragmentManager,
                        VerificationDialog::class.TAG
                    )
                    else -> longToast(R.string.general_failure_message)
                }
            }
            Loading -> viewModel.setProgressIndicationVisibility(true)
        }
    }
}