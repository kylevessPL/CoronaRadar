package pl.piasta.coronaradar.ui.main.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.ActivityMainBinding
import pl.piasta.coronaradar.ui.base.BaseActivity
import pl.piasta.coronaradar.ui.main.viewmodel.MainViewModel
import pl.piasta.coronaradar.ui.settings.view.SettingsActivity
import pl.piasta.coronaradar.ui.util.observeNotNull
import splitties.activities.start
import splitties.resources.txt
import splitties.toast.toast

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView
    private lateinit var connectivitySnackbar: Snackbar

    override val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.appBarMain.toolbar)
        setupNavDrawer()
        setupView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                start<SettingsActivity>()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.appBarMain.contentMain.navHostMain.id) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setupView() {
        connectivitySnackbar = Snackbar.make(
            findViewById(android.R.id.content),
            txt(R.string.network_lost),
            Snackbar.LENGTH_INDEFINITE
        )
    }

    private fun setupNavDrawer() {
        navView = binding.navView
        val drawerLayout: DrawerLayout = binding.drawerLayout
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_radar,
                R.id.nav_history,
                R.id.nav_stats,
                R.id.nav_account,
                R.id.nav_login,
                R.id.nav_settings
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        setupWithNavController(navView, navController)
    }

    override fun updateUI() {
        viewModel.connectivity.observeNotNull(this@MainActivity, { isConnected ->
            switchConnectivitySnackbarDisplay(isConnected)
        })
        viewModel.connectivitySnackbarDisplay.observeNotNull(this@MainActivity, { display ->
            displayConnectivitySnackbar(display)
        })
        viewModel.firebaseUser.observeNotNull(this@MainActivity, { user ->
            switchNavMenu(user)
        })
    }

    private fun switchConnectivitySnackbarDisplay(isConnected: Boolean) =
        viewModel.setConnectivitySnackbarDisplay(!isConnected)

    private fun switchNavMenu(user: FirebaseUser?) {
        navView.menu.clear()
        user?.also {
            navView.inflateMenu(R.menu.activity_main_logged_in_drawer)
        } ?: run {
            navView.inflateMenu(R.menu.activity_main_not_logged_in_drawer)
        }
    }

    private fun displayConnectivitySnackbar(display: Boolean) {
        when (display) {
            true -> connectivitySnackbar.show()
            false -> {
                connectivitySnackbar.dismiss()
                toast(R.string.network_restored)
            }
        }
    }
}
