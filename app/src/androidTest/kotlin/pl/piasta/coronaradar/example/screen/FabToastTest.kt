package pl.piasta.coronaradar.example.screen

import androidx.test.core.app.ActivityScenario
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import de.mannodermaus.junit5.ActivityScenarioExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import pl.piasta.coronaradar.ui.example.view.ExampleActivity

class FabToastTest : TestCase() {

    @JvmField
    @RegisterExtension
    val scenarioExtension = ActivityScenarioExtension.launch<ExampleActivity>()

    @Test
    fun test(scenario: ActivityScenario<ExampleActivity>) {
        run {
            ExampleScreen {
                fab {
                    isVisible()
                    click()
                }

                snackbar {
                    isDisplayed()
                    text.hasText("Example text")
                }
            }
        }
    }
}