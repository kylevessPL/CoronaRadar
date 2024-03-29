package pl.piasta.coronaradar.ui.main.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.ActivityMainBinding
import pl.piasta.coronaradar.ui.base.BaseActivity
import pl.piasta.coronaradar.ui.main.viewmodel.MainViewModel
import pl.piasta.coronaradar.ui.settings.view.SettingsActivity
import splitties.activities.start

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView

    override val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
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

    override fun onAfterLocaleChanged() {
        super.onAfterLocaleChanged()
        navView.menu[0].isChecked = true
    }

    override fun onSupportNavigateUp() =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    override fun setupView() {
        setupActionBar()
        setupNavController()
        setupNavDrawer()
    }

    override fun updateUI() {
        viewModel.firebaseUser.observe(this) {
            switchNavMenu(it)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.appBarMain.toolbar)
        setTitle(R.string.app_name)
    }

    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.appBarMain.contentMain.navHostMain.id) as NavHostFragment
        navController = navHostFragment.navController
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

    private fun switchNavMenu(user: FirebaseUser?) {
        navView.menu.setGroupVisible(R.id.menu_group_logged_in, user != null)
        navView.menu.setGroupVisible(R.id.menu_group_not_logged_in, user == null)
    }
}
