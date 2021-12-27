package pl.piasta.coronaradar.ui.user.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.Observer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import pl.piasta.coronaradar.data.auth.model.ActionCode
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.ui.base.BaseViewModelTest
import pl.piasta.coronaradar.ui.util.observeForTesting
import pl.piasta.coronaradar.util.ResultState

class UserViewModelTest : BaseViewModelTest({

    val coroutineDispatcher = StandardTestDispatcher()

    val application: Application = mockk(relaxed = true)
    val authRepository: AuthRepository = mockk(relaxUnitFun = true)
    val actionCodeData: Uri = mockk()

    runTest {
        given("uri with valid action code") {
            val oob = "oob"
            val verifyActionCodeResultObserver: Observer<ResultState<ActionCode?>> =
                mockk(relaxUnitFun = true)
            every { authRepository.verifyActionCode(any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success(ActionCode.VerifyEmail(oob)))
            }
            val viewModel = UserViewModel(coroutineDispatcher, application, authRepository)

            `when`("verify action code") {
                with(viewModel) {
                    verifyActionCodeResult.observeForTesting(verifyActionCodeResultObserver) {
                        verifyActionCode(actionCodeData)
                    }
                }

                then("verifyActionCodeResult state updates properly") {
                    verifyOrder {
                        verifyActionCodeResultObserver.onChanged(ResultState.Loading)
                        verifyActionCodeResultObserver.onChanged(
                            ResultState.Success(
                                ActionCode.VerifyEmail(
                                    oob
                                )
                            )
                        )
                    }
                }
            }
        }

        given("uri with invalid action code") {
            val verifyActionCodeResultObserver: Observer<ResultState<ActionCode?>> =
                mockk(relaxUnitFun = true)
            val ex: Exception = mockk()
            every { authRepository.verifyActionCode(any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Error(ex))
            }
            val viewModel = UserViewModel(coroutineDispatcher, application, authRepository)

            `when`("verify action code") {
                with(viewModel) {
                    verifyActionCodeResult.observeForTesting(verifyActionCodeResultObserver) {
                        verifyActionCode(actionCodeData)
                    }
                }

                then("verifyActionCodeResult state updates properly") {
                    verifyOrder {
                        verifyActionCodeResultObserver.onChanged(ResultState.Loading)
                        verifyActionCodeResultObserver.onChanged(ResultState.Error(ex))
                    }
                }
            }
        }

        given("not verified user") {
            val verificationEmailResultObserver: Observer<ResultState<Nothing>> =
                mockk(relaxUnitFun = true)
            every { authRepository.sendVerificationEmail() } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success())
            }
            val viewModel = UserViewModel(coroutineDispatcher, application, authRepository)

            `when`("verify email") {
                with(viewModel) {
                    verificationEmailResult.observeForTesting(verificationEmailResultObserver) {
                        sendVerificationEmail()
                    }
                }

                then("verificationEmailResult state updates properly") {
                    verifyOrder {
                        verificationEmailResultObserver.onChanged(ResultState.Loading)
                        verificationEmailResultObserver.onChanged(ResultState.Success())
                    }
                }
            }
        }

        given("verify email action code") {
            val actionCode = "oob"
            val verifyEmailResultObserver: Observer<ResultState<Nothing>> =
                mockk(relaxUnitFun = true)
            every { authRepository.verifyEmail(any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success())
            }
            val viewModel = UserViewModel(coroutineDispatcher, application, authRepository)

            `when`("verify email") {
                with(viewModel) {
                    verifyEmailResult.observeForTesting(verifyEmailResultObserver) {
                        verifyEmail(actionCode)
                    }
                }

                then("verifyEmailResult state updates properly") {
                    verifyOrder {
                        verifyEmailResultObserver.onChanged(ResultState.Loading)
                        verifyEmailResultObserver.onChanged(ResultState.Success())
                    }
                }
            }
        }

        given("email of user which exists in database") {
            val email = "email@example.com"
            val passwordResetEmailResultObserver: Observer<ResultState<Boolean>> =
                mockk(relaxUnitFun = true)
            every { authRepository.sendPasswordResetEmail(any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success(true))
            }
            val viewModel = UserViewModel(coroutineDispatcher, application, authRepository)

            `when`("send password reset email") {
                with(viewModel) {
                    passwordResetEmailResult.observeForTesting(passwordResetEmailResultObserver) {
                        sendPasswordResetEmail(email)
                    }
                }

                then("passwordResetEmailResult state updates properly") {
                    verifyOrder {
                        passwordResetEmailResultObserver.onChanged(ResultState.Loading)
                        passwordResetEmailResultObserver.onChanged(ResultState.Success(true))
                    }
                }
            }
        }

        given("email of user which does not exist in database") {
            val email = "email@example.com"
            val passwordResetEmailResultObserver: Observer<ResultState<Boolean>> =
                mockk(relaxUnitFun = true)
            every { authRepository.sendPasswordResetEmail(any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success(false))
            }
            val viewModel = UserViewModel(coroutineDispatcher, application, authRepository)

            `when`("send password reset email") {
                with(viewModel) {
                    passwordResetEmailResult.observeForTesting(passwordResetEmailResultObserver) {
                        sendPasswordResetEmail(email)
                    }
                }

                then("passwordResetEmailResult state updates properly") {
                    verifyOrder {
                        passwordResetEmailResultObserver.onChanged(ResultState.Loading)
                        passwordResetEmailResultObserver.onChanged(ResultState.Success(false))
                    }
                }
            }
        }

        given("password reset action code and new password") {
            val actionCode = "oob"
            val newPassword = "password"
            val passwordResetResultObserver: Observer<ResultState<Nothing>> =
                mockk(relaxUnitFun = true)
            every { authRepository.resetPassword(any(), any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success())
            }
            val viewModel = UserViewModel(coroutineDispatcher, application, authRepository)

            `when`("send password reset email") {
                with(viewModel) {
                    passwordResetResult.observeForTesting(passwordResetResultObserver) {
                        resetPassword(actionCode, newPassword)
                    }
                }

                then("passwordResetResult state updates properly") {
                    verifyOrder {
                        passwordResetResultObserver.onChanged(ResultState.Loading)
                        passwordResetResultObserver.onChanged(ResultState.Success())
                    }
                }
            }
        }

        given("logged in user") {
            val signOutResultObserver: Observer<ResultState<Nothing>> =
                mockk(relaxUnitFun = true)
            every { authRepository.logout() } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success())
            }
            val viewModel = UserViewModel(coroutineDispatcher, application, authRepository)

            `when`("sign out") {
                with(viewModel) {
                    signOutResult.observeForTesting(signOutResultObserver) {
                        signOut()
                    }
                }

                then("signOutResult state updates properly") {
                    verifyOrder {
                        signOutResultObserver.onChanged(ResultState.Loading)
                        signOutResultObserver.onChanged(ResultState.Success())
                    }
                }
            }
        }
    }
})