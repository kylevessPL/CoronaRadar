package pl.piasta.coronaradar.ui.account.viewmodel

import android.app.Application
import io.kotest.matchers.ints.shouldBeExactly
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import pl.piasta.coronaradar.BaseViewModelTest
import pl.piasta.coronaradar.data.auth.model.UserDetails
import pl.piasta.coronaradar.data.auth.repository.AuthRepository

class AccountViewModelTest : BaseViewModelTest() {

    @RelaxedMockK
    lateinit var application: Application

    @MockK(relaxUnitFun = true)
    lateinit var repository: AuthRepository

    lateinit var viewModel: AccountViewModel

    init {

        beforeTest {
            MockKAnnotations.init(this)
            every { repository.currentUser } returns UserDetails("user", "user@example.com", null)
            viewModel = AccountViewModel(application, repository)
        }

        given("integer value of 3") {
            val x = 3

            `when`("multiply it by 2") {
                val res = x.times(2)

                then("result is 6") {
                    res shouldBeExactly (6)
                }
            }

            `when`("subtract it by 2") {
                val res = x.minus(2)

                then("result should be 1") {
                    res.shouldBeExactly(1)
                }
            }
        }
    }
}