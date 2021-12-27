package pl.piasta.coronaradar.ui.user.view

import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuthActionCodeException
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.auth.model.ActionCode
import pl.piasta.coronaradar.data.auth.model.ActionCode.PasswordReset
import pl.piasta.coronaradar.data.auth.model.ActionCode.VerifyEmail
import pl.piasta.coronaradar.databinding.ActivityUserBinding
import pl.piasta.coronaradar.ui.base.BaseActivity
import pl.piasta.coronaradar.ui.user.viewmodel.UserViewModel
import pl.piasta.coronaradar.ui.util.dispatchActionDownTouchEvent
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.ResultState.*
import pl.piasta.coronaradar.util.TAG
import splitties.toast.longToast

@AndroidEntryPoint
class UserActivity : BaseActivity<ActivityUserBinding, UserViewModel>(R.layout.activity_user) {

    override val viewModel: UserViewModel by viewModels()

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        window.dispatchActionDownTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }

    override fun setupView() {
        processActionCode()
        setupActionBar()
        setupNavController()
    }

    override fun updateUI() {
        viewModel.verifyActionCodeResult.observeNotNull(this) {
            displayVerifyActionCodeResult(it)
        }
        viewModel.verificationEmailResult.observeNotNull(this) {
            displayVerificationEmailResult(it)
        }
        viewModel.passwordResetEmailResult.observeNotNull(this) {
            displayPasswordResetEmailResult(it)
        }
        viewModel.passwordResetResult.observeNotNull(this) {
            displayPasswordResetResult(it)
        }
    }

    private fun processActionCode() {
        lifecycleScope.launchWhenStarted {
            intent.data?.let { viewModel.verifyActionCode(it) }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        setTitle(R.string.my_account)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.contentUser.navHostUser.id) as NavHostFragment
        val graph = navHostFragment.navController.graph
        graph.startDestination = when (viewModel.firebaseUser.value) {
            null -> R.id.nav_login
            else -> R.id.nav_account
        }
        navHostFragment.navController.graph = graph
    }

    private fun displayVerifyActionCodeResult(result: ResultState<ActionCode?>) {
        when (result) {
            is Success -> {
                when (val data = result.data) {
                    is PasswordReset -> {
                        viewModel.setProgressIndicatorVisibility(false)
                        PasswordResetDialogFragment(data.oob).show(
                            supportFragmentManager,
                            PasswordResetDialogFragment::class.TAG
                        )
                    }
                    is VerifyEmail -> viewModel.verifyEmail(data.oob)
                    else -> {}
                }
            }
            is Error -> {
                viewModel.setProgressIndicatorVisibility(false)
                when (result.ex) {
                    is FirebaseAuthActionCodeException -> longToast(R.string.auth_code_failure_invalid_message)
                    else -> longToast(R.string.general_failure_message)
                }
            }
            Loading -> viewModel.setProgressIndicatorVisibility(true)
        }
    }

    private fun displayVerificationEmailResult(result: ResultState<Nothing>) = when (result) {
        is Success -> {
            viewModel.setProgressIndicatorVisibility(false)
            longToast(R.string.verification_email_success_message)
        }
        is Error -> {
            viewModel.setProgressIndicatorVisibility(false)
            longToast(R.string.general_failure_message)
        }
        Loading -> viewModel.setProgressIndicatorVisibility(true)
    }

    private fun displayPasswordResetEmailResult(result: ResultState<Boolean>) = when (result) {
        is Success -> {
            viewModel.setProgressIndicatorVisibility(false)
            longToast(R.string.password_reset_email_complete_message)
        }
        is Error -> {
            viewModel.setProgressIndicatorVisibility(false)
            longToast(R.string.general_failure_message)
        }
        Loading -> viewModel.setProgressIndicatorVisibility(true)
    }

    private fun displayPasswordResetResult(result: ResultState<Boolean>) = when (result) {
        is Success -> {
            viewModel.setProgressIndicatorVisibility(false)
            longToast(R.string.password_reset_complete_message)
        }
        is Error -> {
            viewModel.setProgressIndicatorVisibility(false)
            longToast(R.string.general_failure_message)
        }
        Loading -> viewModel.setProgressIndicatorVisibility(true)
    }
}
