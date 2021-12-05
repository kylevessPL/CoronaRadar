package pl.piasta.coronaradar.data.survey.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pl.piasta.coronaradar.data.common.ResultLabel
import pl.piasta.coronaradar.data.survey.model.Statistics
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.util.ResultState

interface SurveyRepository {

    fun watchStatistics(): Flow<Statistics>
    fun getAllSurveysPaged(): Flow<PagingData<Survey>>
    fun createSurvey(survey: Survey): Flow<ResultState<Nothing>>
    fun uploadCoughAudio(label: ResultLabel, byteArray: ByteArray): Flow<ResultState<Nothing>>
}