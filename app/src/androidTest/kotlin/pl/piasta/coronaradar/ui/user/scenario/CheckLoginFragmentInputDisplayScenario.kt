package pl.piasta.coronaradar.ui.user.scenario

import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.user.screen.UserScreen
import pl.piasta.coronaradar.ui.util.*
import pl.piasta.coronaradar.ui.util.EndIconMode.PASSWORD_TOGGLE

class CheckLoginFragmentInputDisplayScenario<ScenarioData> : BaseScenario<ScenarioData>() {

    override val steps: TestContext<ScenarioData>.() -> Unit = {
        step("Check if all inputs are displayed properly in password input hidden state") {
            UserScreen {
                emailLoginInput {
                    isHelperTextDisabled()
                    isHintEnabled()
                    hasHint(R.string.email)
                    isErrorEnabled()
                    hasNoError()
                }
                passwordLoginInput {
                    isHelperTextDisabled()
                    isHintEnabled()
                    hasHint(R.string.password)
                    isErrorEnabled()
                    hasNoError()
                    isCounterDisabled()
                    hasEndIconMode(PASSWORD_TOGGLE)
                    edit {
                        hasInputType(TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD)
                        isPasswordHidden()
                    }
                }
            }
        }
        step("Check if password input is displayed properly in shown state") {
            UserScreen {
                togglePasswordButton {
                    click()
                }
                passwordLoginInput {
                    edit {
                        isPasswordShown()
                    }
                }
            }
        }
        step("Check if password input is displayed properly in hidden state") {
            UserScreen {
                togglePasswordButton {
                    click()
                }
                passwordLoginInput {
                    edit {
                        isPasswordHidden()
                    }
                }
            }
        }
    }
}