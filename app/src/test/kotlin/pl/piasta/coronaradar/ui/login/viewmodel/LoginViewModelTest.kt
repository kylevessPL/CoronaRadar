package pl.piasta.coronaradar.ui.login.viewmodel

import androidx.lifecycle.Observer
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
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

class LoginViewModelTest : BaseViewModelTest({

    val coroutineDispatcher = UnconfinedTestDispatcher()

    val authRepository: AuthRepository = mockk(relaxUnitFun = true)
    val firebaseUser: FirebaseUser = mockk()

    given("email and password") {
        val email = "email@example.com"
        val password = "password"
        val signInResultObserver: Observer<ResultState<FirebaseUser>> =
            mockk(relaxUnitFun = true)
        every { authRepository.login(any(), any()) } returns flow {
            emit(ResultState.Loading)
            emit(ResultState.Success(firebaseUser))
        }
        val viewModel = LoginViewModel(coroutineDispatcher, authRepository).apply {
            loginForm.input.email.set(email)
            loginForm.input.password.set(password)
        }

        `when`("login user with email and password") {
            with(viewModel) {
                signInResult.observeForTesting(signInResultObserver) {
                    signIn()
                }
            }

            then("signInResult state updates properly") {
                verifyOrder {
                    signInResultObserver.onChanged(ResultState.Loading)
                    signInResultObserver.onChanged(ResultState.Success(firebaseUser))
                }
            }

            then("login repository function is invoked") {
                coVerify {
                    authRepository.login(email, password)
                }
            }
        }
    }

    given("GoogleSignInAccount task") {
        val task: Task<GoogleSignInAccount> = mockk(relaxed = true)
        val signInResultObserver: Observer<ResultState<FirebaseUser>> =
            mockk(relaxUnitFun = true)
        every { authRepository.loginWithGoogle(any()) } returns flow {
            emit(ResultState.Loading)
            emit(ResultState.Success(firebaseUser))
        }
        val viewModel = LoginViewModel(coroutineDispatcher, authRepository)

        `when`("login user with Google") {
            with(viewModel) {
                signInResult.observeForTesting(signInResultObserver) {
                    signInWithGoogle(task)
                }
            }

            then("signInResult state updates properly") {
                verifyOrder {
                    signInResultObserver.onChanged(ResultState.Loading)
                    signInResultObserver.onChanged(ResultState.Success(firebaseUser))
                }
            }

            then("login repository function is invoked") {
                coVerify {
                    authRepository.loginWithGoogle(task)
                }
            }
        }
    }

    given("facebook CallbackManager") {
        val callbackManager: CallbackManager = mockk(relaxed = true)
        val signInResultObserver: Observer<ResultState<FirebaseUser>> =
            mockk(relaxUnitFun = true)
        every { authRepository.loginWithFacebook(any()) } returns flow {
            emit(ResultState.Loading)
            emit(ResultState.Success(firebaseUser))
        }
        val viewModel = LoginViewModel(coroutineDispatcher, authRepository)

        `when`("login user with Facebook") {
            with(viewModel) {
                signInResult.observeForTesting(signInResultObserver) {
                    signInWithFacebook(callbackManager)
                }
            }

            then("signInResult state updates properly") {
                verifyOrder {
                    signInResultObserver.onChanged(ResultState.Loading)
                    signInResultObserver.onChanged(ResultState.Success(firebaseUser))
                }
            }

            then("login repository function is invoked") {
                coVerify {
                    authRepository.loginWithFacebook(callbackManager)
                }
            }
        }
    }
})