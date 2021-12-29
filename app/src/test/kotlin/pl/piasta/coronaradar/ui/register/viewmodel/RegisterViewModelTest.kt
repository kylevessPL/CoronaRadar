package pl.piasta.coronaradar.ui.register.viewmodel

import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseUser
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.ui.base.BaseViewModelTest
import pl.piasta.coronaradar.ui.util.observeForTesting
import pl.piasta.coronaradar.util.ResultState

class RegisterViewModelTest : BaseViewModelTest({

    val coroutineDispatcher = UnconfinedTestDispatcher()

    val authRepository: AuthRepository = mockk(relaxUnitFun = true)
    val firebaseUser: FirebaseUser = mockk()

    given("email and password") {
        val email = "email@example.com"
        val password = "password"
        val signUpResultObserver: Observer<ResultState<FirebaseUser>> =
            mockk(relaxUnitFun = true)
        every { authRepository.register(any(), any()) } returns flow {
            emit(ResultState.Loading)
            emit(ResultState.Success(firebaseUser))
        }
        val viewModel = RegisterViewModel(coroutineDispatcher, authRepository).apply {
            registerForm.input.email.set(email)
            registerForm.input.password.set(password)
            registerForm.input.passwordConfirm.set(password)
        }

        `when`("register user with email and password") {
            with(viewModel) {
                signUpResult.observeForTesting(signUpResultObserver) {
                    signUp()
                }
            }

            then("signUpResult state updates properly") {
                verifyOrder {
                    signUpResultObserver.onChanged(ResultState.Loading)
                    signUpResultObserver.onChanged(ResultState.Success(firebaseUser))
                }
            }

            then("register repository function is invoked") {
                coVerify {
                    authRepository.register(email, password)
                }
            }
        }
    }
})