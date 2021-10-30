package pl.piasta.coronaradar.ui.user.view

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuthActionCodeException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.auth.model.ActionCode.PasswordReset
import pl.piasta.coronaradar.data.auth.model.ActionCode.VerifyEmail
import pl.piasta.coronaradar.databinding.ActivityUserBinding
import pl.piasta.coronaradar.ui.base.BaseActivity
import pl.piasta.coronaradar.ui.user.viewmodel.UserViewModel
import pl.piasta.coronaradar.ui.util.dispatchActionDownTouchEvent
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.ResultState.Error
import pl.piasta.coronaradar.util.ResultState.Loading
import pl.piasta.coronaradar.util.ResultState.Success
import pl.piasta.coronaradar.util.TAG
import pl.piasta.coronaradar.util.ifTrue
import splitties.toast.longToast

@AndroidEntryPoint
class UserActivity : BaseActivity<ActivityUserBinding, UserViewModel>(R.layout.activity_user) {

    override val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        lifecycleScope.launchWhenStarted {
            withContext(Dispatchers.IO) {
                intent.data?.let { viewModel.verifyActionCode(it) }
            }
        }
    }

    override fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.contentUser.navHostUser.id) as NavHostFragment
        val graph = navHostFragment.navController.graph
        graph.startDestination = when (viewModel.firebaseUser.value) {
            null -> R.id.nav_login
            else -> R.id.nav_account
        }
        navHostFragment.navController.graph = graph
    }

    override fun updateUI() {
        viewModel.verifyActionCodeResult.observeNotNull(this@UserActivity, { result ->
            when (result) {
                is Success -> {
                    when (val data = result.data) {
                        is PasswordReset -> {
                            viewModel.setProgressIndicationVisibility(false)
                            PasswordResetDialog(data.oob).show(
                                supportFragmentManager,
                                PasswordResetDialog::class.TAG
                            )
                        }
                        is VerifyEmail -> viewModel.verifyEmail(data.oob)
                    }
                }
                is Error -> {
                    viewModel.setProgressIndicationVisibility(false)
                    when (result.ex) {
                        is FirebaseAuthActionCodeException -> longToast(R.string.auth_code_failure_invalid_message)
                        else -> longToast(R.string.general_failure_message)
                    }
                }
                Loading -> viewModel.setProgressIndicationVisibility(true)
            }
        })
        viewModel.verificationEmailResult.observeNotNull(
            this@UserActivity,
            { displayVerificationEmailResult(it) })
        viewModel.verifyEmailResult.observeNotNull(this@UserActivity, { result ->
            when (result) {
                Loading -> viewModel.setProgressIndicationVisibility(true)
                else -> {
                    viewModel.setProgressIndicationVisibility(false)
                    (result is Error).ifTrue { longToast(R.string.general_failure_message) }
                }
            }
        })
        viewModel.passwordResetEmailResult.observeNotNull(
            this,
            { displayPasswordResetEmailResult(it) })
        viewModel.passwordResetResult.observeNotNull(
            this,
            { displayPasswordResetResult(it) })
    }

    private fun displayVerificationEmailResult(result: ResultState<Nothing>) = when (result) {
        is Success -> {
            viewModel.setProgressIndicationVisibility(false)
            longToast(R.string.verification_email_success_message)
        }
        is Error -> {
            viewModel.setProgressIndicationVisibility(false)
            longToast(R.string.general_failure_message)
        }
        Loading -> viewModel.setProgressIndicationVisibility(true)
    }

    private fun displayPasswordResetEmailResult(result: ResultState<Boolean>) = when (result) {
        is Success -> {
            viewModel.setProgressIndicationVisibility(false)
            longToast(R.string.password_reset_email_complete_message)
        }
        is Error -> {
            viewModel.setProgressIndicationVisibility(false)
            longToast(R.string.general_failure_message)
        }
        Loading -> viewModel.setProgressIndicationVisibility(true)
    }

    private fun displayPasswordResetResult(result: ResultState<Boolean>) = when (result) {
        is Success -> {
            viewModel.setProgressIndicationVisibility(false)
            longToast(R.string.password_reset_complete_message)
        }
        is Error -> {
            viewModel.setProgressIndicationVisibility(false)
            longToast(R.string.general_failure_message)
        }
        Loading -> viewModel.setProgressIndicationVisibility(true)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        window.dispatchActionDownTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }
}
