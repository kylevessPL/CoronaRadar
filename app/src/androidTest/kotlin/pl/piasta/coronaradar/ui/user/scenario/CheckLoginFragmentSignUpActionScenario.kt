package pl.piasta.coronaradar.ui.user.scenario

import androidx.navigation.NavController
import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.hamcrest.MatcherAssert.assertThat
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.user.screen.UserScreen

class CheckLoginFragmentSignUpActionScenario<ScenarioData>(navController: NavController) :
    BaseScenario<ScenarioData>() {

    override val steps: TestContext<ScenarioData>.() -> Unit = {
        step("Check if sign up action is displayed properly") {
            UserScreen {
                signUpAction {
                    isVisible()
                    hasTextColor(R.color.text_link_color_state)
                }
            }
        }
        step("Check if sign up action navigates properly") {
            UserScreen {
                signUpAction {
                    click()
                }
            }
            assertThat(
                "Destination does not match",
                navController.currentDestination?.id == R.id.nav_register
            )
        }
    }
}