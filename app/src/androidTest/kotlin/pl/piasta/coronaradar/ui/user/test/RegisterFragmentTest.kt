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
import pl.piasta.coronaradar.ui.register.view.RegisterFragment
import pl.piasta.coronaradar.ui.user.scenario.CheckRegisterFragmentInputDisplayScenario
import pl.piasta.coronaradar.ui.user.scenario.CheckRegisterFragmentSignInActionScenario
import pl.piasta.coronaradar.ui.user.scenario.CheckRegisterFragmentValidationScenario

@HiltAndroidTest
class RegisterFragmentTest : TestCase() {

    @get:Rule
    var rule = HiltAndroidRule(this)

    private val navController = TestNavHostController(device.targetContext)

    @Before
    fun setup() {
        launchFragmentInContainer<RegisterFragment>(Bundle.EMPTY, R.style.Theme_CoronaRadar).apply {
            onFragment { fragment ->
                navController.setGraph(R.navigation.nav_graph_user)
                navController.setCurrentDestination(R.id.nav_register)
                Navigation.setViewNavController(fragment.requireView(), navController)
            }
        }
    }

    @Test
    fun `Should navigate to Login fragment on sign in link action`() = run {
        scenario(CheckRegisterFragmentSignInActionScenario(navController))
    }

    @Test
    fun `All inputs should be displayed properly`() = run {
        scenario(CheckRegisterFragmentInputDisplayScenario())
    }

    @Test
    fun `Any field input should trigger validation mechanism`() = run {
        scenario(CheckRegisterFragmentValidationScenario())
    }
}