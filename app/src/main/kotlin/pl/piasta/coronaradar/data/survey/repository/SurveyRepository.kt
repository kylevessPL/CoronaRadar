package pl.piasta.coronaradar.data.survey.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.util.ResultState

interface SurveyRepository {

    fun getAllSurveysPaged(): Flow<PagingData<Survey>>
    fun createSurvey(survey: Survey): Flow<ResultState<Nothing>>
}