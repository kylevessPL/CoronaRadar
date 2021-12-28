package pl.piasta.coronaradar.ui.user.scenario

import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.user.screen.UserScreen

class CheckLoginFragmentForgotPasswordActionScenario<ScenarioData> : BaseScenario<ScenarioData>() {

    override val steps: TestContext<ScenarioData>.() -> Unit = {
        step("Check if forgot password is displayed properly") {
            UserScreen {
                forgotPasswordAction {
                    isVisible()
                    hasTextColor(R.color.text_link_color_state)
                }
            }
        }
        step("Check if forgot password action shows dialog") {
            UserScreen {
                forgotPasswordAction {
                    click()
                }
                flakySafely {
                    alertDialog {
                        isDisplayed()
                        title {
                            hasText(R.string.password_reset)
                        }
                        message {
                            hasText(R.string.password_reset_email_message)
                        }
                        positiveButton {
                            isDisplayed()
                            hasText(R.string.send)
                        }
                        negativeButton {
                            isDisplayed()
                        }
                    }
                }
            }
        }
    }
}