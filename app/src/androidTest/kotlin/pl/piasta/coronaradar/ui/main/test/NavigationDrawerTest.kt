package pl.piasta.coronaradar.ui.main.test

import androidx.test.ext.junit.rules.activityScenarioRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.main.scenario.CheckNavigationDrawerDisplayScenario
import pl.piasta.coronaradar.ui.main.screen.MainScreen
import pl.piasta.coronaradar.ui.main.view.MainActivity
import pl.piasta.coronaradar.ui.settings.view.SettingsActivity

@HiltAndroidTest
class NavigationDrawerTest : TestCase() {

    @get:Rule
    var rule: TestRule = RuleChain
        .outerRule(HiltAndroidRule(this))
        .around(activityScenarioRule<MainActivity>())

    @Test
    fun `Navigation drawer should display properly`() = run {
        scenario(CheckNavigationDrawerDisplayScenario())
    }

    @Test
    fun `Radar item action should navigate to Radar fragment`() = run {
        MainScreen {
            drawerLayout {
                close()
                open()
            }
            navDrawer {
                navigateTo(R.id.nav_radar)
            }
            toolbar {
                hasTitle(R.string.app_name)
            }
            flakySafely {
                radarFragment {
                    isDisplayed()
                }
            }
        }
    }

    @Test
    fun `Statistics item action should navigate to Stats fragment`() = run {
        MainScreen {
            drawerLayout {
                close()
                open()
            }
            navDrawer {
                navigateTo(R.id.nav_stats)
            }
            toolbar {
                hasTitle(R.string.stats)
            }
            flakySafely {
                statsFragment {
                    isDisplayed()
                }
            }
        }
    }

    @Test
    fun `Settings item action should navigate to Settings fragment`() = run {
        MainScreen {
            drawerLayout {
                close()
                open()
            }
            navDrawer {
                navigateTo(R.id.nav_settings)
            }
            flakySafely {
                device.activities.isCurrent(SettingsActivity::class.java)
            }
        }
    }
}