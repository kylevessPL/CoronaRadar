package pl.piasta.coronaradar.ui.account.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.Observer
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import pl.piasta.coronaradar.data.auth.model.UserDetails
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.ui.base.BaseViewModelTest
import pl.piasta.coronaradar.ui.util.observeMultipleForTesting
import pl.piasta.coronaradar.util.EMPTY
import pl.piasta.coronaradar.util.ResultState
import java.io.ByteArrayInputStream

class AccountViewModelTest : BaseViewModelTest({

    val coroutineDispatcher = UnconfinedTestDispatcher()

    val application: Application = mockk(relaxed = true)
    val authRepository: AuthRepository = mockk(relaxUnitFun = true)
    val avatarUri: Uri = mockk()

    given("logged in user and avatar uri") {
        println(coroutineContext.toString())
        val avatarBytes = "test data".toByteArray()
        val displayNameEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
        val passwordEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
        val uploadUserAvatarResultObserver: Observer<ResultState<Uri>> =
            mockk(relaxUnitFun = true)
        val updateUserProfileResultObserver: Observer<ResultState<Nothing>> =
            mockk(relaxUnitFun = true)
        every { application.contentResolver.openInputStream(any()) } returns ByteArrayInputStream(
            avatarBytes
        )
        every { authRepository.currentUser } returns UserDetails(String.EMPTY, String.EMPTY)
        every { authRepository.uploadAvatar(any()) } returns flow {
            emit(ResultState.Loading)
            emit(ResultState.Success(avatarUri))
        }
        every { authRepository.updateCurrentUserDetails(any(), any()) } returns flow {
            emit(ResultState.Loading)
            emit(ResultState.Success())
        }
        val viewModel =
            AccountViewModel(coroutineDispatcher, application, authRepository).apply {
                userDetailsForm.input.avatar.set(avatarUri)
            }

        `when`("update user avatar only") {
            with(viewModel) {
                mapOf(
                    displayNameEnabled to displayNameEnabledObserver,
                    passwordEnabled to passwordEnabledObserver,
                    uploadUserAvatarResult to uploadUserAvatarResultObserver,
                    updateUserProfileResult to updateUserProfileResultObserver
                ).observeMultipleForTesting {
                    updateProfile()
                }
            }

            then("displayNameEnabled state updates properly") {
                viewModel.displayNameEnabled.value shouldBe false
            }

            then("passwordEnabled state updates properly") {
                viewModel.passwordEnabled.value shouldBe false
            }

            then("uploadUserAvatarResult state updates properly") {
                verifyOrder {
                    uploadUserAvatarResultObserver.onChanged(ResultState.Loading)
                    uploadUserAvatarResultObserver.onChanged(ResultState.Success(avatarUri))
                }
            }

            then("updateUserProfileResult state updates properly") {
                verifyOrder {
                    updateUserProfileResultObserver.onChanged(ResultState.Loading)
                    updateUserProfileResultObserver.onChanged(ResultState.Success())
                }
            }

            then("only upload avatar and update details repository function is invoked") {
                coVerify {
                    authRepository.uploadAvatar(avatarBytes)
                    authRepository.updateCurrentUserDetails(any(), avatarUri)
                }
                coVerify(inverse = true) {
                    authRepository.updateCurrentUserPassword(any())
                }
            }
        }
    }

    given("logged in user, display name and password") {
        val displayName = "Name Surname"
        val email = "email@example.com"
        val password = "password"
        val displayNameEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
        val passwordEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
        val uploadUserAvatarResultObserver: Observer<ResultState<Uri>> =
            mockk(relaxUnitFun = true)
        val updateUserProfileResultObserver: Observer<ResultState<Nothing>> =
            mockk(relaxUnitFun = true)
        every { authRepository.currentUser } returns UserDetails(displayName, email)
        every { authRepository.updateCurrentUserPassword(any()) } returns flow {
            emit(ResultState.Loading)
            emit(ResultState.Success())
        }
        every { authRepository.updateCurrentUserDetails(any(), any()) } returns flow {
            emit(ResultState.Loading)
            emit(ResultState.Success())
        }
        val viewModel =
            AccountViewModel(coroutineDispatcher, application, authRepository).apply {
                userDetailsForm.input.let {
                    it.initialDisplayName = String.EMPTY
                    it.displayName.set(displayName)
                    it.password.set(password)
                    it.passwordConfirm.set(password)
                }
            }

        `when`("update display name and passsword") {
            with(viewModel) {
                mapOf(
                    displayNameEnabled to displayNameEnabledObserver,
                    passwordEnabled to passwordEnabledObserver,
                    uploadUserAvatarResult to uploadUserAvatarResultObserver,
                    updateUserProfileResult to updateUserProfileResultObserver
                ).observeMultipleForTesting {
                    updateProfile()
                }
            }

            then("displayNameEnabled state updates properly") {
                viewModel.displayNameEnabled.value shouldBe false
            }

            then("passwordEnabled state updates properly") {
                viewModel.passwordEnabled.value shouldBe false
            }

            then("uploadUserAvatarResult state doesn't receive any updates") {
                verify(inverse = true) {
                    uploadUserAvatarResultObserver.onChanged(any())
                }
            }

            then("updateUserProfileResult state updates properly") {
                verifyOrder {
                    updateUserProfileResultObserver.onChanged(ResultState.Loading)
                    updateUserProfileResultObserver.onChanged(ResultState.Success())
                }
            }

            then("only update password and details repository functions are invoked") {
                coVerify {
                    authRepository.updateCurrentUserPassword(password)
                    authRepository.updateCurrentUserDetails(displayName)
                }
                coVerify(inverse = true) {
                    authRepository.uploadAvatar(any())
                }
            }
        }
    }

    given("logged in user and password") {
        val password = "password"
        val displayNameEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
        val passwordEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
        val uploadUserAvatarResultObserver: Observer<ResultState<Uri>> =
            mockk(relaxUnitFun = true)
        val updateUserProfileResultObserver: Observer<ResultState<Nothing>> =
            mockk(relaxUnitFun = true)
        every { authRepository.currentUser } returns UserDetails(String.EMPTY, String.EMPTY)
        every { authRepository.updateCurrentUserPassword(any()) } returns flow {
            emit(ResultState.Loading)
            emit(ResultState.Success())
        }
        val viewModel =
            AccountViewModel(coroutineDispatcher, application, authRepository).apply {
                userDetailsForm.input.let {
                    it.password.set(password)
                    it.passwordConfirm.set(password)
                }
            }

        `when`("update user passsword") {
            with(viewModel) {
                mapOf(
                    displayNameEnabled to displayNameEnabledObserver,
                    passwordEnabled to passwordEnabledObserver,
                    uploadUserAvatarResult to uploadUserAvatarResultObserver,
                    updateUserProfileResult to updateUserProfileResultObserver
                ).observeMultipleForTesting {
                    updateProfile()
                }
            }

            then("displayNameEnabled state updates properly") {
                viewModel.displayNameEnabled.value shouldBe false
            }

            then("passwordEnabled state updates properly") {
                viewModel.passwordEnabled.value shouldBe false
            }

            then("uploadUserAvatarResult state doesn't receive any updates") {
                verify(inverse = true) {
                    uploadUserAvatarResultObserver.onChanged(any())
                }
            }

            then("updateUserProfileResult state updates properly") {
                verifyOrder {
                    updateUserProfileResultObserver.onChanged(ResultState.Loading)
                    updateUserProfileResultObserver.onChanged(ResultState.Success())
                }
            }

            then("only update password repository function is invoked") {
                coVerify {
                    authRepository.updateCurrentUserPassword(password)
                }
                coVerify(inverse = true) {
                    authRepository.uploadAvatar(any())
                    authRepository.updateCurrentUserDetails(any(), any())
                }
            }
        }
    }

    given("logged in user, avatar uri, display name and password") {
        val avatarBytes = "test data".toByteArray()
        val displayName = "Name Surname"
        val email = "email@example.com"
        val password = "password"
        val displayNameEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
        val passwordEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
        val uploadUserAvatarResultObserver: Observer<ResultState<Uri>> =
            mockk(relaxUnitFun = true)
        val updateUserProfileResultObserver: Observer<ResultState<Nothing>> =
            mockk(relaxUnitFun = true)
        every { application.contentResolver.openInputStream(any()) } returns ByteArrayInputStream(
            avatarBytes
        )
        every { authRepository.currentUser } returns UserDetails(displayName, email, avatarUri)
        every { authRepository.uploadAvatar(any()) } returns flow {
            emit(ResultState.Loading)
            emit(ResultState.Success(avatarUri))
        }
        every { authRepository.updateCurrentUserPassword(any()) } returns flow {
            emit(ResultState.Loading)
            emit(ResultState.Success())
        }
        every { authRepository.updateCurrentUserDetails(any(), any()) } returns flow {
            emit(ResultState.Loading)
            emit(ResultState.Success())
        }
        val viewModel =
            AccountViewModel(coroutineDispatcher, application, authRepository).apply {
                userDetailsForm.input.let {
                    it.avatar.set(avatarUri)
                    it.initialDisplayName = String.EMPTY
                    it.displayName.set(displayName)
                    it.password.set(password)
                    it.passwordConfirm.set(password)
                }
            }

        `when`("update user avatar, display name and passsword") {
            with(viewModel) {
                mapOf(
                    displayNameEnabled to displayNameEnabledObserver,
                    passwordEnabled to passwordEnabledObserver,
                    uploadUserAvatarResult to uploadUserAvatarResultObserver,
                    updateUserProfileResult to updateUserProfileResultObserver
                ).observeMultipleForTesting {
                    updateProfile()
                }
            }

            then("displayNameEnabled state updates properly") {
                viewModel.displayNameEnabled.value shouldBe false
            }

            then("passwordEnabled state updates properly") {
                viewModel.passwordEnabled.value shouldBe false
            }

            then("uploadUserAvatarResult state updates properly") {
                verifyOrder {
                    uploadUserAvatarResultObserver.onChanged(ResultState.Loading)
                    uploadUserAvatarResultObserver.onChanged(ResultState.Success(avatarUri))
                }
            }

            then("updateUserProfileResult state updates properly") {
                verifyOrder {
                    updateUserProfileResultObserver.onChanged(ResultState.Loading)
                    updateUserProfileResultObserver.onChanged(ResultState.Success())
                }
            }

            then("all repository functions invoked") {
                coVerify {
                    authRepository.uploadAvatar(avatarBytes)
                    authRepository.updateCurrentUserPassword(password)
                    authRepository.updateCurrentUserDetails(displayName, avatarUri)
                }
            }
        }
    }
})