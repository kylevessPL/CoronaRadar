package pl.piasta.coronaradar.ui.settings.test

import androidx.test.ext.junit.rules.activityScenarioRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import pl.piasta.coronaradar.ui.settings.scenario.CheckLanguagePreferenceActionScenario
import pl.piasta.coronaradar.ui.settings.screen.PreferenceItem
import pl.piasta.coronaradar.ui.settings.screen.SettingsScreen
import pl.piasta.coronaradar.ui.settings.view.SettingsActivity

@HiltAndroidTest
class SettingsPreferencesTest : TestCase() {

    @get:Rule
    var rule: TestRule = RuleChain
        .outerRule(HiltAndroidRule(this))
        .around(activityScenarioRule<SettingsActivity>())

    @Test
    fun `Preferences view should show all preferences`() = run {
        SettingsScreen {
            preferenceList {
                children<PreferenceItem> {
                    languagePreference {
                        isVisible()
                    }
                    coronaKitUpdateFrequencyPreference {
                        isVisible()
                    }
                    coronaKitUpdateWifiOnlyPreference {
                        isVisible()
                    }
                }
            }
        }
    }

    @Test
    fun `Language preference action should change display language`() = run {
        scenario(CheckLanguagePreferenceActionScenario())
    }
}