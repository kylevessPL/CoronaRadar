package pl.piasta.coronaradar.ui.user.scenario

import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.user.screen.UserScreen
import pl.piasta.coronaradar.ui.user.util.INVALID_EMAIL
import pl.piasta.coronaradar.ui.user.util.INVALID_PASSWORD
import pl.piasta.coronaradar.ui.user.util.VALID_EMAIL
import pl.piasta.coronaradar.ui.user.util.VALID_PASSWORD

class CheckRegisterFragmentValidationScenario<ScenarioData> : BaseScenario<ScenarioData>() {

    override val steps: TestContext<ScenarioData>.() -> Unit = {
        step("Type invalid email and check if error is present on email input and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                emailRegisterInput {
                    click()
                    edit {
                        replaceText(INVALID_EMAIL)
                        pressImeAction()
                    }
                }
                flakySafely {
                    emailRegisterInput {
                        hasError(R.string.email_not_valid)
                    }
                    passwordRegisterInput {
                        hasNoError()
                    }
                    passwordConfirmRegisterInput {
                        hasNoError()
                    }
                    signUpButton {
                        isDisabled()
                    }
                }
            }
        }
        step("Leave empty password and check if error is present on email and password input and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                emailRegisterInput {
                    click()
                }
                flakySafely {
                    emailRegisterInput {
                        hasError(R.string.email_not_valid)
                    }
                    passwordRegisterInput {
                        hasError(R.string.empty_not_allowed)
                    }
                    passwordConfirmRegisterInput {
                        hasNoError()
                    }
                    signUpButton {
                        isDisabled()
                    }
                }
            }
        }
        step("Type too short password and check if error is present on email and password input and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                passwordRegisterInput {
                    click()
                    edit {
                        replaceText(INVALID_PASSWORD)
                    }
                }
                emailRegisterInput {
                    click()
                }
                flakySafely {
                    emailRegisterInput {
                        hasError(R.string.email_not_valid)
                    }
                    passwordRegisterInput {
                        hasError(R.string.min_six_chars_allowed)
                    }
                    passwordConfirmRegisterInput {
                        hasNoError()
                    }
                    signUpButton {
                        isDisabled()
                    }
                }
            }
        }
        step("Leave empty password confirm and check if error is present on all inputs and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                passwordConfirmRegisterInput {
                    click()
                    edit {
                        pressImeAction()
                    }
                }
                flakySafely {
                    emailRegisterInput {
                        hasError(R.string.email_not_valid)
                    }
                    passwordRegisterInput {
                        hasError(R.string.min_six_chars_allowed)
                    }
                    passwordConfirmRegisterInput {
                        hasError(R.string.empty_not_allowed)
                    }
                    signUpButton {
                        isDisabled()
                    }
                }
            }
        }
        step("Type valid password and not matching password confirm and check if error is present on but password input and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                passwordRegisterInput {
                    click()
                    edit {
                        replaceText(VALID_PASSWORD)
                        pressImeAction()
                    }
                }
                passwordConfirmRegisterInput {
                    click()
                    edit {
                        replaceText(INVALID_PASSWORD)
                        pressImeAction()
                    }
                }
                flakySafely {
                    emailRegisterInput {
                        hasError(R.string.email_not_valid)
                    }
                    passwordRegisterInput {
                        hasNoError()
                    }
                    passwordConfirmRegisterInput {
                        hasError(R.string.passwords_not_match)
                    }
                    signUpButton {
                        isDisabled()
                    }
                }
            }
        }
        step("Leave empty password and check if error is present on all but password confirm input and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                passwordRegisterInput {
                    click()
                    edit {
                        clearText()
                        pressImeAction()
                    }
                }
                flakySafely {
                    emailRegisterInput {
                        hasError(R.string.email_not_valid)
                    }
                    passwordRegisterInput {
                        hasError(R.string.empty_not_allowed)
                    }
                    passwordConfirmRegisterInput {
                        hasNoError()
                    }
                    signUpButton {
                        isDisabled()
                    }
                }
            }
        }
        step("Type valid password and leave not matching password confirm, then clear password only and check if error is present on all but password confirm input and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                passwordRegisterInput {
                    click()
                    edit {
                        replaceText(VALID_PASSWORD)
                        pressImeAction()
                    }
                }
                passwordConfirmRegisterInput {
                    edit {
                        pressImeAction()
                    }
                }
                passwordRegisterInput {
                    click()
                    edit {
                        clearText()
                        pressImeAction()
                    }
                }
                flakySafely {
                    emailRegisterInput {
                        hasError(R.string.email_not_valid)
                    }
                    passwordRegisterInput {
                        hasError(R.string.empty_not_allowed)
                    }
                    passwordConfirmRegisterInput {
                        hasNoError()
                    }
                    signUpButton {
                        isDisabled()
                    }
                }
            }
        }
        step("Type valid password and matching password confirm and check if error is present on email input only and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                passwordRegisterInput {
                    click()
                    edit {
                        replaceText(VALID_PASSWORD)
                        pressImeAction()
                    }
                }
                passwordConfirmRegisterInput {
                    click()
                    edit {
                        replaceText(VALID_PASSWORD)
                        pressImeAction()
                    }
                }
                flakySafely {
                    emailRegisterInput {
                        hasError(R.string.email_not_valid)
                    }
                    passwordRegisterInput {
                        hasNoError()
                    }
                    passwordConfirmRegisterInput {
                        hasNoError()
                    }
                    signUpButton {
                        isDisabled()
                    }
                }
            }
        }
        step("Type valid email and check if error is not present on all inputs and button is enabled") {
            UserScreen {
                closeSoftKeyboard()
                emailRegisterInput {
                    click()
                    edit {
                        replaceText(VALID_EMAIL)
                        pressImeAction()
                    }
                }
                flakySafely {
                    emailRegisterInput {
                        hasNoError()
                    }
                    passwordRegisterInput {
                        hasNoError()
                    }
                    passwordConfirmRegisterInput {
                        hasNoError()
                    }
                    signUpButton {
                        isEnabled()
                    }
                }
            }
        }
        step("Type empty email and check if error is present on email input only and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                emailRegisterInput {
                    click()
                    edit {
                        clearText()
                        pressImeAction()
                    }
                }
                flakySafely {
                    emailRegisterInput {
                        hasError(R.string.empty_not_allowed)
                    }
                    passwordRegisterInput {
                        hasNoError()
                    }
                    passwordConfirmRegisterInput {
                        hasNoError()
                    }
                    signUpButton {
                        isDisabled()
                    }
                }
            }
        }
    }
}