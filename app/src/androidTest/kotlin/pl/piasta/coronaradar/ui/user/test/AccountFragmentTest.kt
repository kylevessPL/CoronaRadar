package pl.piasta.coronaradar.ui.user.test

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.intent.Intents
import com.google.firebase.auth.FirebaseUser
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import it.czerwinski.android.hilt.fragment.testing.launchFragmentInContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.account.view.AccountFragment
import pl.piasta.coronaradar.ui.user.scenario.CheckAccountFragmentInputDisplayScenario
import pl.piasta.coronaradar.ui.user.scenario.CheckAccountFragmentUpdateAvatarScenario
import pl.piasta.coronaradar.ui.user.scenario.CheckAccountFragmentValidationScenario
import pl.piasta.coronaradar.ui.user.screen.UserScreen
import pl.piasta.coronaradar.ui.user.viewmodel.UserViewModel

@HiltAndroidTest
class AccountFragmentTest : TestCase() {

    @get:Rule
    var rule = HiltAndroidRule(this)

    private val navController = TestNavHostController(device.targetContext)

    @Before
    fun setup() {
        val viewModelFactory = mockk<ViewModelProvider.Factory>()
        val parentViewModel = mockk<UserViewModel>(relaxed = true)
        val user = mockk<FirebaseUser>()
        every { viewModelFactory.create(UserViewModel::class.java) } returns parentViewModel
        every { parentViewModel.firebaseUser } returns MutableLiveData<FirebaseUser>(user)
        launchFragmentInContainer(Bundle.EMPTY, R.style.Theme_CoronaRadar) {
            AccountFragment { viewModelFactory }.also { fragment ->
                fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        navController.setGraph(R.navigation.nav_graph_user)
                        navController.setCurrentDestination(R.id.nav_account)
                        Navigation.setViewNavController(fragment.requireView(), navController)
                    }
                }
            }
        }
    }

    @Test
    fun `Sign out dialog should be displayed properly`() = run {
        UserScreen {
            signOutButton {
                isVisible()
                click()
            }
            flakySafely {
                alertDialog {
                    isDisplayed()
                    title {
                        hasText(R.string.signout)
                    }
                    message {
                        hasText(R.string.signout_message)
                    }
                    positiveButton {
                        isVisible()
                        hasText(R.string.signout)
                    }
                    negativeButton {
                        isVisible()
                    }
                }
            }
        }
    }

    @Test
    fun `All inputs should be displayed properly`() = run {
        scenario(CheckAccountFragmentInputDisplayScenario())
    }

    @Test
    fun `Update avatar action should trigger update avatar mechanism`() =
        before {
            Intents.init()
        }.after {
            Intents.release()
        }.run {
            scenario(CheckAccountFragmentUpdateAvatarScenario())
        }

    @Test
    fun `Any field input should trigger validation mechanism`() = run {
        scenario(CheckAccountFragmentValidationScenario())
    }
}