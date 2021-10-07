package pl.piasta.coronaradar.ui.account

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.ActivityAccountBinding
import pl.piasta.coronaradar.ui.base.BaseActivity

@AndroidEntryPoint
class AccountActivity : BaseActivity<ActivityAccountBinding, Nothing>(R.layout.activity_account) {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupNavController()
    }

    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.contentAccount.navHostAccount.id) as NavHostFragment
        navController = navHostFragment.navController
    }
}
