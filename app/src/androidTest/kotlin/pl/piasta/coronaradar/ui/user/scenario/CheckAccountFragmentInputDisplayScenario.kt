package pl.piasta.coronaradar.ui.user.scenario

import android.text.InputType.*
import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.user.screen.UserScreen
import pl.piasta.coronaradar.ui.util.*
import pl.piasta.coronaradar.ui.util.EndIconMode.CUSTOM
import pl.piasta.coronaradar.ui.util.EndIconMode.NONE

class CheckAccountFragmentInputDisplayScenario<ScenarioData> : BaseScenario<ScenarioData>() {

    override val steps: TestContext<ScenarioData>.() -> Unit = {
        step("Check if all inputs are displayed properly in not editable state") {
            UserScreen {
                displayNameAccountInput {
                    hasPlaceholderText(R.string.display_name)
                    isHelperTextDisabled()
                    isHintEnabled()
                    hasHint(R.string.display_name)
                    isExpandedHintDisabled()
                    isErrorEnabled()
                    hasNoError()
                    edit {
                        isDisabled()
                    }
                }
                emailAccountInput {
                    hasPlaceholderText(R.string.email)
                    isHelperTextDisabled()
                    isHintEnabled()
                    hasHint(R.string.email)
                    isExpandedHintDisabled()
                    isErrorEnabled()
                    hasNoError()
                    edit {
                        hasInputType(TYPE_NULL)
                    }
                }
                passwordAccountInput {
                    hasPlaceholderText(R.string.password_new)
                    isHelperTextDisabled()
                    isHintEnabled()
                    hasHint(R.string.password)
                    isExpandedHintDisabled()
                    isErrorEnabled()
                    hasNoError()
                    isCounterDisabled()
                    hasEndIconMode(CUSTOM)
                    edit {
                        isDisabled()
                        hasInputType(TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                        isPasswordShown()
                    }
                }
                passwordConfirmAccountInput {
                    hasPlaceholderText(R.string.password_new_confirm)
                    isHelperTextDisabled()
                    isHintEnabled()
                    hasHint(R.string.password_confirm)
                    isExpandedHintDisabled()
                    isErrorEnabled()
                    hasNoError()
                    isCounterDisabled()
                    hasEndIconMode(NONE)
                    edit {
                        isDisabled()
                        hasInputType(TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                        isPasswordShown()
                    }
                }
            }
        }
        step("Check if all inputs are displayed properly in editable state") {
            UserScreen {
                displayNameEditButton {
                    click()
                }
                passwordEditButton {
                    click()
                }
                displayNameAccountInput {
                    edit {
                        isEnabled()
                    }
                }
                passwordAccountInput {
                    edit {
                        isEnabled()
                        isPasswordShown()
                    }
                }
                passwordConfirmAccountInput {
                    edit {
                        isEnabled()
                        isPasswordShown()
                    }
                }
            }
        }
        step("Check if all inputs are displayed properly in editable state again") {
            UserScreen {
                displayNameEditButton {
                    click()
                }
                passwordEditButton {
                    click()
                }
                displayNameAccountInput {
                    edit {
                        isDisabled()
                    }
                }
                passwordAccountInput {
                    edit {
                        isDisabled()
                    }
                }
                passwordConfirmAccountInput {
                    edit {
                        isDisabled()
                    }
                }
            }
        }
    }
}