package pl.piasta.coronaradar.ui.main.test

import androidx.test.ext.junit.rules.activityScenarioRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import pl.piasta.coronaradar.ui.main.scenario.CheckLanguageChangeScenario
import pl.piasta.coronaradar.ui.main.view.MainActivity

@HiltAndroidTest
class MainStackTest : TestCase() {

    @get:Rule
    var rule: TestRule = RuleChain
        .outerRule(HiltAndroidRule(this))
        .around(activityScenarioRule<MainActivity>())

    @Test
    fun `Main activity should apply language changes after Settings back button is pressed`() =
        run {
            scenario(CheckLanguageChangeScenario())
        }
}