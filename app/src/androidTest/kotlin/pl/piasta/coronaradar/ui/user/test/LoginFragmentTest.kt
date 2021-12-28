package pl.piasta.coronaradar.ui.user.test

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import it.czerwinski.android.hilt.fragment.testing.launchFragmentInContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.login.view.LoginFragment
import pl.piasta.coronaradar.ui.user.scenario.CheckLoginFragmentForgotPasswordActionScenario
import pl.piasta.coronaradar.ui.user.scenario.CheckLoginFragmentInputDisplayScenario
import pl.piasta.coronaradar.ui.user.scenario.CheckLoginFragmentSignUpActionScenario
import pl.piasta.coronaradar.ui.user.scenario.CheckLoginFragmentValidationScenario

@HiltAndroidTest
class LoginFragmentTest : TestCase() {

    @get:Rule
    var rule = HiltAndroidRule(this)

    private val navController = TestNavHostController(device.targetContext)

    @Before
    fun setup() {
        launchFragmentInContainer<LoginFragment>(Bundle.EMPTY, R.style.Theme_CoronaRadar).apply {
            onFragment { fragment ->
                navController.setGraph(R.navigation.nav_graph_user)
                navController.setCurrentDestination(R.id.nav_login)
                Navigation.setViewNavController(fragment.requireView(), navController)
            }
        }
    }

    @Test
    fun `Sign up link action should navigate to Register fragment`() = run {
        scenario(CheckLoginFragmentSignUpActionScenario(navController))
    }

    @Test
    fun `Forgot password link action should show dialog`() = run {
        scenario(CheckLoginFragmentForgotPasswordActionScenario())
    }

    @Test
    fun `All inputs should be displayed properly`() = run {
        scenario(CheckLoginFragmentInputDisplayScenario())
    }

    @Test
    fun `Any field input should trigger validation mechanism`() = run {
        scenario(CheckLoginFragmentValidationScenario())
    }
}