package pl.piasta.coronaradar.ui.user.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.auth.model.ActionCode.PasswordReset
import pl.piasta.coronaradar.data.auth.model.ActionCode.VerifyEmail
import pl.piasta.coronaradar.databinding.ActivityUserBinding
import pl.piasta.coronaradar.ui.base.BaseActivity
import pl.piasta.coronaradar.ui.user.viewmodel.UserViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState.Error
import pl.piasta.coronaradar.util.ResultState.Loading
import pl.piasta.coronaradar.util.ResultState.Success
import pl.piasta.coronaradar.util.TAG
import pl.piasta.coronaradar.util.ifTrue
import splitties.toast.longToast
import javax.inject.Inject

@AndroidEntryPoint
class UserActivity : BaseActivity<ActivityUserBinding, UserViewModel>(R.layout.activity_user) {

    @Inject
    lateinit var firebaseDynamicLinks: FirebaseDynamicLinks

    private lateinit var navController: NavController

    override val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        lifecycleScope.launchWhenStarted {
            val task = firebaseDynamicLinks.getDynamicLink(intent)
            withContext(Dispatchers.IO) {
                viewModel.verifyActionCode(task)
            }
        }
    }

    override fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.contentUser.navHostUser.id) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun updateUI() {
        viewModel.verifyActionCodeResult.observeNotNull(this@UserActivity, { result ->
            when (result) {
                is Success -> when (val data = result.data!!) {
                    is PasswordReset -> PasswordResetDialog(data.oob).show(
                        supportFragmentManager,
                        PasswordResetDialog::class.TAG
                    )
                    is VerifyEmail -> viewModel.verifyEmail(data.oob)
                    else -> viewModel.setProgressIndicationVisibility(false)
                }
                is Error -> {
                    viewModel.setProgressIndicationVisibility(false)
                    longToast(R.string.general_failure_message)
                }
                Loading -> viewModel.setProgressIndicationVisibility(true)
            }
        })
        viewModel.verifyEmailResult.observeNotNull(this@UserActivity, { result ->
            when (result) {
                Loading -> viewModel.setProgressIndicationVisibility(true)
                else -> {
                    viewModel.setProgressIndicationVisibility(false)
                    (result is Error).ifTrue { longToast(R.string.general_failure_message) }
                }
            }
        })
    }
}
