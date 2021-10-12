package pl.piasta.coronaradar.ui.account

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.ActivityAccountBinding
import pl.piasta.coronaradar.ui.base.BaseActivity
import javax.inject.Inject

@AndroidEntryPoint
class AccountActivity : BaseActivity<ActivityAccountBinding, Nothing>(R.layout.activity_account) {

    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupNavController()
        // var action = AccountFragmentDirections.actionAccountFragmentToLoginFragment()
        // if (auth.currentUser == null) {
        //     navController.navigate(action)
        //     // action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        //     // navController.navigate(action)
        // }
    }

    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.contentAccount.navHostAccount.id) as NavHostFragment
        navController = navHostFragment.navController
    }
}
