package pl.piasta.coronaradar.ui.user.scenario

import android.text.InputType
import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.user.screen.UserScreen
import pl.piasta.coronaradar.ui.util.*

class CheckRegisterFragmentInputDisplayScenario<ScenarioData> : BaseScenario<ScenarioData>() {

    override val steps: TestContext<ScenarioData>.() -> Unit = {
        step("Check if all inputs are displayed properly in all password inputs hidden state") {
            UserScreen {
                emailRegisterInput {
                    isHelperTextDisabled()
                    isHintEnabled()
                    hasHint(R.string.email)
                    isErrorEnabled()
                    hasNoError()
                }
                passwordRegisterInput {
                    isHelperTextEnabled()
                    hasHelperText(R.string.min_six_chars_allowed)
                    isHintEnabled()
                    hasHint(R.string.password)
                    isErrorEnabled()
                    hasNoError()
                    isCounterEnabled()
                    hasEndIconMode(EndIconMode.PASSWORD_TOGGLE)
                    edit {
                        hasInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        isPasswordHidden()
                    }
                }
                passwordConfirmRegisterInput {
                    isHelperTextDisabled()
                    isHintEnabled()
                    hasHint(R.string.password_confirm)
                    isErrorEnabled()
                    hasNoError()
                    isCounterDisabled()
                    hasEndIconMode(EndIconMode.PASSWORD_TOGGLE)
                    edit {
                        hasInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
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
                passwordRegisterInput {
                    edit {
                        isPasswordShown()
                    }
                }
                passwordConfirmRegisterInput {
                    edit {
                        isPasswordHidden()
                    }
                }
            }
        }
        step("Check if password input is displayed properly in hidden state") {
            UserScreen {
                togglePasswordButton {
                    click()
                }
                passwordRegisterInput {
                    edit {
                        isPasswordHidden()
                    }
                }
                passwordConfirmRegisterInput {
                    edit {
                        isPasswordHidden()
                    }
                }
            }
        }
        step("Check if password confirm input is displayed properly in shown state") {
            UserScreen {
                togglePasswordConfirmButton {
                    click()
                }
                passwordRegisterInput {
                    edit {
                        isPasswordHidden()
                    }
                }
                passwordConfirmRegisterInput {
                    edit {
                        isPasswordShown()
                    }
                }
            }
        }
        step("Check if password confirm input is displayed properly in hidden state") {
            UserScreen {
                togglePasswordConfirmButton {
                    click()
                }
                passwordRegisterInput {
                    edit {
                        isPasswordHidden()
                    }
                }
                passwordConfirmRegisterInput {
                    edit {
                        isPasswordHidden()
                    }
                }
            }
        }
    }
}