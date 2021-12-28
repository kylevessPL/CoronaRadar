package pl.piasta.coronaradar.ui.user.scenario

import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.user.screen.UserScreen
import pl.piasta.coronaradar.ui.user.util.INVALID_EMAIL
import pl.piasta.coronaradar.ui.user.util.VALID_EMAIL
import pl.piasta.coronaradar.ui.user.util.VALID_PASSWORD

class CheckLoginFragmentValidationScenario<ScenarioData> : BaseScenario<ScenarioData>() {

    override val steps: TestContext<ScenarioData>.() -> Unit = {
        step("Type invalid email and check if error is present on email input and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                emailLoginInput {
                    click()
                    edit {
                        replaceText(INVALID_EMAIL)
                        pressImeAction()
                    }
                }
                flakySafely {
                    emailLoginInput {
                        hasError(R.string.email_not_valid)
                    }
                    passwordLoginInput {
                        hasNoError()
                    }
                    signInButton {
                        isDisabled()
                    }
                }
            }
        }
        step("Leave empty password and check if error is present on all inputs and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                passwordLoginInput {
                    edit {
                        pressImeAction()
                    }
                }
                flakySafely {
                    emailLoginInput {
                        hasError(R.string.email_not_valid)
                    }
                    passwordLoginInput {
                        hasError(R.string.empty_not_allowed)
                    }
                    signInButton {
                        isDisabled()
                    }
                }
            }
        }
        step("Type valid email and check if error is present on all but email input and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                emailLoginInput {
                    click()
                    edit {
                        replaceText(VALID_EMAIL)
                        pressImeAction()
                    }
                }
                flakySafely {
                    emailLoginInput {
                        hasNoError()
                    }
                    passwordLoginInput {
                        hasError(R.string.empty_not_allowed)
                    }
                    signInButton {
                        isDisabled()
                    }
                }
            }
        }
        step("Type valid email and some password and check if error is not present on all inputs and button is enabled") {
            UserScreen {
                closeSoftKeyboard()
                emailLoginInput {
                    click()
                    edit {
                        replaceText(VALID_EMAIL)
                        pressImeAction()
                    }
                }
                passwordLoginInput {
                    edit {
                        replaceText(VALID_PASSWORD)
                        pressImeAction()
                    }
                }
                flakySafely {
                    emailLoginInput {
                        hasNoError()
                    }
                    passwordLoginInput {
                        hasNoError()
                    }
                    signInButton {
                        isEnabled()
                    }
                }
            }
        }
        step("Type empty email and some password and check if error is present on all but password input and button is disabled") {
            UserScreen {
                closeSoftKeyboard()
                emailLoginInput {
                    click()
                    edit {
                        clearText()
                        pressImeAction()
                    }
                }
                flakySafely {
                    emailLoginInput {
                        hasError(R.string.empty_not_allowed)
                    }
                    passwordLoginInput {
                        hasNoError()
                    }
                    signInButton {
                        isDisabled()
                    }
                }
            }
        }
    }
}