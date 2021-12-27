package pl.piasta.coronaradar.ui.main.scenario

import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.main.screen.MainScreen
import pl.piasta.coronaradar.ui.main.view.MainActivity
import pl.piasta.coronaradar.ui.settings.screen.SettingsScreen
import pl.piasta.coronaradar.ui.settings.view.SettingsActivity
import pl.piasta.coronaradar.ui.util.strLocalized
import pl.piasta.coronaradar.util.supportedLocales
import splitties.activities.start

class CheckLanguageChangeScenario<ScenarioData> : BaseScenario<ScenarioData>() {

    override val steps: TestContext<ScenarioData>.() -> Unit = {
        step("Set initial English language in main activity") {
            (device.activities.getResumed() as? MainActivity)!!.apply {
                runOnUiThread {
                    setLanguage(supportedLocales[0])
                }
            }
            flakySafely {
                MainScreen {
                    helpCardTitle {
                        hasText(
                            device.targetContext.strLocalized(
                                R.string.tip,
                                supportedLocales[0]
                            )
                        )
                    }
                }
            }
        }
        step("Navigate to Settings activity and change language to Polish") {
            device.activities.getResumed()!!.start<SettingsActivity>()
            flakySafely {
                device.activities.isCurrent(SettingsActivity::class.java)
            }
            (device.activities.getResumed() as? SettingsActivity)!!.apply {
                runOnUiThread {
                    setLanguage(supportedLocales[1])
                }
            }
            flakySafely {
                SettingsScreen {
                    toolbar {
                        hasTitle(
                            device.targetContext.strLocalized(
                                R.string.settings,
                                supportedLocales[1]
                            )
                        )
                    }
                }
            }
        }
        step("Navigate back to Main activity and change if display changes are applied") {
            device.uiDevice.pressBack()
            flakySafely {
                MainScreen {
                    helpCardTitle {
                        hasText(
                            device.targetContext.strLocalized(
                                R.string.tip,
                                supportedLocales[1]
                            )
                        )
                    }
                }
            }
        }
    }
}