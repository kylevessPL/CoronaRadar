package pl.piasta.coronaradar.ui.account.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.Observer
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import pl.piasta.coronaradar.data.auth.model.UserDetails
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.ui.base.BaseViewModelTest
import pl.piasta.coronaradar.ui.util.observeMultipleForTesting
import pl.piasta.coronaradar.util.EMPTY
import pl.piasta.coronaradar.util.ResultState
import java.io.ByteArrayInputStream

class AccountViewModelTest : BaseViewModelTest({

    val coroutineDispatcher = TestCoroutineDispatcher()

    val application: Application = mockk(relaxed = true)
    val repository: AuthRepository = mockk(relaxUnitFun = true)

    runBlockingTest {
        given("avatar uri") {
            val avatarBytes = "test data".toByteArray()
            val avatarUri: Uri = mockk()
            val displayNameEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
            val passwordEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
            val uploadUserAvatarResultObserver: Observer<ResultState<Uri>> =
                mockk(relaxUnitFun = true)
            val updateUserProfileResultObserver: Observer<ResultState<Nothing>> =
                mockk(relaxUnitFun = true)
            every { application.contentResolver.openInputStream(any()) } returns ByteArrayInputStream(
                avatarBytes
            )
            every { repository.currentUser } returns UserDetails(String.EMPTY, String.EMPTY)
            every { repository.uploadAvatar(any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success(avatarUri))
            }
            val viewModel = AccountViewModel(coroutineDispatcher, application, repository).apply {
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

                then("updateUserProfileResult state doesn't receive any updates") {
                    verify(inverse = true) {
                        updateUserProfileResultObserver.onChanged(any())
                    }
                }

                then("only upload avatar and update details repository function is invoked") {
                    coVerify {
                        repository.uploadAvatar(avatarBytes)
                        repository.updateCurrentUserDetails(any(), avatarUri)
                    }
                    coVerify(inverse = true) {
                        repository.updateCurrentUserPassword(any())
                    }
                }
            }
        }
    }

    runBlockingTest {
        given("display name and password") {
            val displayName = "Name Surname"
            val email = "email@example.com"
            val password = "password"
            val displayNameEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
            val passwordEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
            val uploadUserAvatarResultObserver: Observer<ResultState<Uri>> =
                mockk(relaxUnitFun = true)
            val updateUserProfileResultObserver: Observer<ResultState<Nothing>> =
                mockk(relaxUnitFun = true)
            every { repository.currentUser } returns UserDetails(displayName, email)
            every { repository.updateCurrentUserPassword(any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success())
            }
            every { repository.updateCurrentUserDetails(any(), any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success())
            }
            val viewModel = AccountViewModel(coroutineDispatcher, application, repository).apply {
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
                    verifyOrder(inverse = true) {
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
                        repository.updateCurrentUserPassword(password)
                        repository.updateCurrentUserDetails(displayName)
                    }
                    coVerify(inverse = true) {
                        repository.uploadAvatar(any())
                    }
                }
            }
        }
    }

    runBlockingTest {
        given("password") {
            val password = "password"
            val displayNameEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
            val passwordEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
            val uploadUserAvatarResultObserver: Observer<ResultState<Uri>> =
                mockk(relaxUnitFun = true)
            val updateUserProfileResultObserver: Observer<ResultState<Nothing>> =
                mockk(relaxUnitFun = true)
            every { repository.currentUser } returns UserDetails(String.EMPTY, String.EMPTY)
            every { repository.updateCurrentUserPassword(any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success())
            }
            val viewModel = AccountViewModel(coroutineDispatcher, application, repository).apply {
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
                    verifyOrder(inverse = true) {
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
                        repository.updateCurrentUserPassword(password)
                    }
                    coVerify(inverse = true) {
                        repository.uploadAvatar(any())
                        repository.updateCurrentUserDetails(any(), any())
                    }
                }
            }
        }
    }

    runBlockingTest {
        given("avatar uri, display name and password") {
            val avatarBytes = "test data".toByteArray()
            val displayName = "Name Surname"
            val email = "email@example.com"
            val password = "password"
            val avatarUri: Uri = mockk()
            val displayNameEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
            val passwordEnabledObserver: Observer<Boolean> = mockk(relaxUnitFun = true)
            val uploadUserAvatarResultObserver: Observer<ResultState<Uri>> =
                mockk(relaxUnitFun = true)
            val updateUserProfileResultObserver: Observer<ResultState<Nothing>> =
                mockk(relaxUnitFun = true)
            every { application.contentResolver.openInputStream(any()) } returns ByteArrayInputStream(
                avatarBytes
            )
            every { repository.currentUser } returns UserDetails(displayName, email, avatarUri)
            every { repository.uploadAvatar(any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success(avatarUri))
            }
            every { repository.updateCurrentUserPassword(any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success())
            }
            every { repository.updateCurrentUserDetails(any(), any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success())
            }
            val viewModel = AccountViewModel(coroutineDispatcher, application, repository).apply {
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
                        repository.uploadAvatar(avatarBytes)
                        repository.updateCurrentUserPassword(password)
                        repository.updateCurrentUserDetails(displayName, avatarUri)
                    }
                }
            }
        }
    }
})