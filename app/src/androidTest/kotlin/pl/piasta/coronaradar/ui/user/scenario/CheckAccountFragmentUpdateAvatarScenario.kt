package pl.piasta.coronaradar.ui.user.scenario

import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import androidx.core.content.FileProvider
import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.user.screen.UserScreen
import pl.piasta.coronaradar.ui.util.hasDrawable
import java.io.File

class CheckAccountFragmentUpdateAvatarScenario<ScenarioData> : BaseScenario<ScenarioData>() {

    companion object {
        private const val ACCEPTED_IMAGE = "sample_below_2mb.png"
        private const val NOT_ACCEPTED_IMAGE = "sample_above_2mb.png"
    }

    private val tmpDir = "/data/local/tmp"

    override val steps: TestContext<ScenarioData>.() -> Unit = {
        step("Check if avatar image does not change if image is not selected") {
            UserScreen {
                chooseImageIntent {
                    intending {
                        withCode(RESULT_CANCELED)
                    }
                }
                updateAvatarButon {
                    click()
                }
                flakySafely {
                    avatarImage {
                        hasDrawable(R.drawable.ic_avatar_140)
                    }
                }
            }
        }
        step("Check if avatar image does not change and dialog displayed if image is selected and of not accepted size") {
            val imageUri = FileProvider.getUriForFile(
                device.targetContext,
                "pl.piasta.coronaradar.fileprovider",
                File("$tmpDir/$NOT_ACCEPTED_IMAGE")
            )
            device.files.push(NOT_ACCEPTED_IMAGE, tmpDir)
            UserScreen {
                chooseImageIntent {
                    intending {
                        withData(Intent().apply {
                            data = imageUri
                        })
                    }
                }
                updateAvatarButon {
                    click()
                }
                flakySafely {
                    alertDialog {
                        isDisplayed()
                        title {
                            hasText(R.string.set_avatar)
                        }
                        message {
                            hasText(R.string.set_avatar_size_failure)
                        }
                        negativeButton {
                            isVisible()
                        }
                        positiveButton {
                            isVisible()
                            click()
                        }
                    }
                    avatarImage {
                        hasDrawable(R.drawable.ic_avatar_140)
                    }
                }
            }
        }
        step("Check if avatar image does change if image is selected and of accepted size") {
            val imageUri = FileProvider.getUriForFile(
                device.targetContext,
                "pl.piasta.coronaradar.fileprovider",
                File("$tmpDir/$ACCEPTED_IMAGE")
            )
            device.files.push(ACCEPTED_IMAGE, tmpDir)
            UserScreen {
                chooseImageIntent {
                    intending {
                        withData(Intent().apply {
                            data = imageUri
                        })
                    }
                }
                updateAvatarButon {
                    click()
                }
                flakySafely {
                    avatarImage {
                        hasDrawable(imageUri)
                    }
                }
            }
        }
    }
}