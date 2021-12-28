package pl.piasta.coronaradar.ui.radar.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.Observer
import com.chaquo.python.Python
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import pl.piasta.coronaradar.data.auth.model.UserDetails
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.data.common.ResultLabel.POSITIVE
import pl.piasta.coronaradar.data.history.repository.HistoryRepository
import pl.piasta.coronaradar.data.ml.repository.MlRepository
import pl.piasta.coronaradar.data.survey.repository.SurveyRepository
import pl.piasta.coronaradar.ui.base.BaseViewModelTest
import pl.piasta.coronaradar.ui.radar.model.Classification
import pl.piasta.coronaradar.ui.util.observeForTesting
import pl.piasta.coronaradar.util.EMPTY
import pl.piasta.coronaradar.util.ResultState

class RadarViewModelTest : BaseViewModelTest({

    val coroutineDispatcher = UnconfinedTestDispatcher()

    val application: Application = mockk(relaxed = true)
    val preferences: SharedPreferences = mockk(relaxUnitFun = true)
    val pythonInterpreter: Python = mockk(relaxUnitFun = true)
    val authRepository: AuthRepository = mockk(relaxUnitFun = true)
    val mlRepository: MlRepository = mockk(relaxUnitFun = true)
    val historyRepository: HistoryRepository = mockk(relaxUnitFun = true)
    val surveyRepository: SurveyRepository = mockk(relaxUnitFun = true)

    runTest {
        given("logged in user and radar classification") {
            val classification = Classification(POSITIVE, 0)
            val saveUserHistoryResultObserver: Observer<ResultState<Nothing>> =
                mockk(relaxUnitFun = true)
            every { authRepository.currentUser } returns UserDetails(String.EMPTY, String.EMPTY)
            every { historyRepository.createHistory(any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success())
            }
            val viewModel = RadarViewModel(
                coroutineDispatcher,
                application,
                authRepository,
                mlRepository,
                historyRepository,
                surveyRepository,
                pythonInterpreter,
                preferences
            )

            `when`("save user history") {
                with(viewModel) {
                    saveUserHistoryResult.observeForTesting(saveUserHistoryResultObserver) {
                        saveUserHistory(classification)
                    }
                }

                then("saveUserHistoryResult state updates properly") {
                    verifyOrder {
                        saveUserHistoryResultObserver.onChanged(ResultState.Loading)
                        saveUserHistoryResultObserver.onChanged(ResultState.Success())
                    }
                }
            }
        }
    }

    runTest {
        given("not logged in user and radar classification") {
            val classification = Classification(POSITIVE, 0)
            val saveUserHistoryResultObserver: Observer<ResultState<Nothing>> =
                mockk(relaxUnitFun = true)
            every { authRepository.currentUser } returns null
            every { historyRepository.createHistory(any()) } returns flow {
                emit(ResultState.Loading)
                emit(ResultState.Success())
            }
            val viewModel = RadarViewModel(
                coroutineDispatcher,
                application,
                authRepository,
                mlRepository,
                historyRepository,
                surveyRepository,
                pythonInterpreter,
                preferences
            )

            `when`("save user history") {
                with(viewModel) {
                    saveUserHistoryResult.observeForTesting(saveUserHistoryResultObserver) {
                        saveUserHistory(classification)
                    }
                }

                then("saveUserHistoryResult state not updated") {
                    verify(inverse = true) {
                        saveUserHistoryResultObserver.onChanged(any())
                    }
                }
            }
        }
    }
})