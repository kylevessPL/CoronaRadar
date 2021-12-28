package pl.piasta.coronaradar.ui.main.scenario

import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.main.screen.MainScreen

class CheckNavigationDrawerDisplayScenario<ScenarioData> : BaseScenario<ScenarioData>() {

    override val steps: TestContext<ScenarioData>.() -> Unit = {
        step("Open navigation drawer") {
            MainScreen {
                hamburgerButton {
                    click()
                }
                navDrawer {
                    isVisible()
                }
            }
        }
        step("Check if navigation drawer contains all items") {
            MainScreen {
                navDrawer {
                    hasDescendant {
                        withId(R.id.nav_radar)
                    }
                    hasDescendant {
                        withId(R.id.nav_stats)
                    }
                    hasDescendant {
                        withId(R.id.nav_settings)
                    }
                }
            }
        }
        step("Check if first item of navigation drawer is checked") {
            MainScreen {
                navDrawer {
                    isItemChecked(0)
                }
            }
        }
    }
}