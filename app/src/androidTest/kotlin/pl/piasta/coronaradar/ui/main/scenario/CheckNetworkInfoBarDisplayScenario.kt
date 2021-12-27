package pl.piasta.coronaradar.ui.main.scenario

import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import pl.piasta.coronaradar.ui.main.screen.MainScreen

class CheckNetworkInfoBarDisplayScenario<ScenarioData> : BaseScenario<ScenarioData>() {

    override val steps: TestContext<ScenarioData>.() -> Unit = {
        step("Disable network") {
            device.network.disable()
            flakySafely {
                MainScreen {
                    networkInfoBar {
                        isVisible()
                    }
                }
            }
        }

        step("Enable network") {
            device.network.enable()
            flakySafely {
                MainScreen {
                    networkInfoBar {
                        isGone()
                    }
                }
            }
        }

        step("Turn on Wi-Fi and turn off mobile data") {
            device.network.toggleMobileData(false)
            device.network.toggleWiFi(true)
            flakySafely {
                MainScreen {
                    networkInfoBar {
                        isGone()
                    }
                }
            }
        }

        step("Turn off Wi-Fi and turn on mobile data") {
            device.network.toggleMobileData(true)
            device.network.toggleWiFi(false)
            flakySafely {
                MainScreen {
                    networkInfoBar {
                        isGone()
                    }
                }
            }
        }
    }
}