package pl.piasta.coronaradar.ui.main.test

import androidx.test.ext.junit.rules.activityScenarioRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import pl.piasta.coronaradar.ui.main.screen.MainScreen
import pl.piasta.coronaradar.ui.main.view.MainActivity
import pl.piasta.coronaradar.ui.settings.view.SettingsActivity

@HiltAndroidTest
class MainToolbarTest : TestCase() {

    @get:Rule
    var rule: TestRule = RuleChain
        .outerRule(HiltAndroidRule(this))
        .around(activityScenarioRule<MainActivity>())

    @Test
    fun `Toolbar settings action should navigate to Settings fragment`() = run {
        MainScreen {
            device.uiDevice.pressMenu()
            settingsAction {
                isDisplayed()
                click()
            }
            flakySafely {
                device.activities.isCurrent(SettingsActivity::class.java)
            }
        }
    }
}