package pl.piasta.coronaradar.ui.user.scenario

import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.user.screen.UserScreen
import pl.piasta.coronaradar.ui.user.util.DISPLAY_NAME
import pl.piasta.coronaradar.ui.user.util.INVALID_PASSWORD
import pl.piasta.coronaradar.ui.user.util.VALID_PASSWORD

class CheckAccountFragmentValidationScenario<ScenarioData> : BaseScenario<ScenarioData>() {

    override val steps: TestContext<ScenarioData>.() -> Unit = {
        step("Type some display name and check if error is not present on all inputs and button is enabled") {
            UserScreen {
                closeSoftKeyboard()
                displayNameAccountInput {
                    click()
                    edit {
                        replaceText(DISPLAY_NAME)
                        pressImeAction()
                    }
                }
                flakySafely {
                    displayNameAccountInput {
                        hasNoError()
                    }
                    passwordAccountInput {
                        hasNoError()
                    }
                    passwordConfirmAccountInput {
                        hasNoError()
                    }
                    saveChangesButton {
                        isEnabled()
                    }
                }
            }
        }
        step("Type too short password and check if error is present on password input only and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                passwordAccountInput {
                    click()
                    edit {
                        replaceText(INVALID_PASSWORD)
                    }
                }
                displayNameAccountInput {
                    click()
                }
                flakySafely {
                    displayNameAccountInput {
                        hasNoError()
                    }
                    passwordAccountInput {
                        hasError(R.string.min_six_chars_allowed)
                    }
                    passwordConfirmAccountInput {
                        hasNoError()
                    }
                    saveChangesButton {
                        isDisabled()
                    }
                }
            }
        }
        step("Leave empty password confirm and check if error is present on password and password confirm inputs and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                passwordConfirmAccountInput {
                    click()
                    edit {
                        pressImeAction()
                    }
                }
                flakySafely {
                    displayNameAccountInput {
                        hasNoError()
                    }
                    passwordAccountInput {
                        hasError(R.string.min_six_chars_allowed)
                    }
                    passwordConfirmAccountInput {
                        hasError(R.string.empty_not_allowed)
                    }
                    saveChangesButton {
                        isDisabled()
                    }
                }
            }
        }
        step("Leave empty password and check if error is present not present on all inputs and button is enabled") {
            UserScreen {
                closeSoftKeyboard()
                passwordAccountInput {
                    click()
                    edit {
                        clearText()
                        pressImeAction()
                    }
                }
                flakySafely {
                    displayNameAccountInput {
                        hasNoError()
                    }
                    passwordAccountInput {
                        hasNoError()
                    }
                    passwordConfirmAccountInput {
                        hasNoError()
                    }
                    saveChangesButton {
                        isEnabled()
                    }
                }
            }
        }
        step("Type valid password and not matching password confirm and check if error is present on password and password confirm inputs and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                passwordAccountInput {
                    click()
                    edit {
                        replaceText(VALID_PASSWORD)
                        pressImeAction()
                    }
                }
                passwordConfirmAccountInput {
                    edit {
                        replaceText(INVALID_PASSWORD)
                        pressImeAction()
                    }
                }
                flakySafely {
                    displayNameAccountInput {
                        hasNoError()
                    }
                    passwordAccountInput {
                        hasNoError()
                    }
                    passwordConfirmAccountInput {
                        hasError(R.string.passwords_not_match)
                    }
                    saveChangesButton {
                        isDisabled()
                    }
                }
            }
        }
        step("Type matching password confirm and check if error is not present on all inputs and button is enabled") {
            UserScreen {
                closeSoftKeyboard()
                passwordConfirmAccountInput {
                    click()
                    edit {
                        replaceText(VALID_PASSWORD)
                        pressImeAction()
                    }
                }
                flakySafely {
                    displayNameAccountInput {
                        hasNoError()
                    }
                    passwordAccountInput {
                        hasNoError()
                    }
                    passwordConfirmAccountInput {
                        hasNoError()
                    }
                    saveChangesButton {
                        isEnabled()
                    }
                }
            }
        }
        step("Type empty email and check if error is not present on all inputs and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                displayNameAccountInput {
                    click()
                    edit {
                        clearText()
                        pressImeAction()
                    }
                }
                flakySafely {
                    displayNameAccountInput {
                        hasNoError()
                    }
                    passwordAccountInput {
                        hasNoError()
                    }
                    passwordConfirmAccountInput {
                        hasNoError()
                    }
                    saveChangesButton {
                        isDisabled()
                    }
                }
            }
        }
    }
}