package pl.piasta.coronaradar.ui.settings.scenario

import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.settings.screen.PreferenceItem
import pl.piasta.coronaradar.ui.settings.screen.SettingsScreen
import pl.piasta.coronaradar.ui.util.supportedLanguagesCodes
import splitties.resources.appStrArray

class CheckLanguagePreferenceActionScenario<ScenarioData> : BaseScenario<ScenarioData>() {

    override val steps: TestContext<ScenarioData>.() -> Unit = {
        step("Open language preference dialog") {
            SettingsScreen {
                preferenceList {
                    firstChild<PreferenceItem> {
                        languagePreference {
                            click()
                        }
                    }
                }
                flakySafely {
                    alertDialog {
                        isDisplayed()
                    }
                }
            }
        }
        step("Check if dialog UI contains all components") {
            SettingsScreen {
                alertDialog {
                    title {
                        hasText(R.string.language)
                    }
                    hasChoiceItems(*appStrArray(R.array.language_entries))
                }
            }
        }
        step("Select first language as initial and check if selected") {
            SettingsScreen {
                alertDialog {
                    onChoiceItem(device.targetContext.supportedLanguagesCodes[0].first) {
                        click()
                    }
                }
                preferenceList {
                    firstChild<PreferenceItem> {
                        languagePreference {
                            click()
                        }
                    }
                }
                flakySafely {
                    alertDialog {
                        onChoiceItem(device.targetContext.supportedLanguagesCodes[0].first) {
                            isChecked()
                        }
                    }
                }
            }
        }
    }
}