package pl.piasta.coronaradar.data.survey.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import pl.piasta.coronaradar.data.common.FirestorePagingSource
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.data.survey.repository.entity.SurveyEntity
import pl.piasta.coronaradar.di.GetAllSurveysPagingQuery
import pl.piasta.coronaradar.di.SurveysCollection
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.TAG
import javax.inject.Inject

class FirestoreSurveyRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val pagingConfig: PagingConfig,
    @SurveysCollection private val surveysCollection: CollectionReference,
    @GetAllSurveysPagingQuery private val getAllSurveysPagingQuery: Query
) : SurveyRepository {

    override fun getAllSurveysPaged() = Pager(pagingConfig) {
        FirestorePagingSource<Survey>(getAllSurveysPagingQuery, SurveyEntity::class.java)
    }.flow.flowOn(Dispatchers.IO)

    override fun createSurvey(survey: Survey): Flow<ResultState<Nothing>> = flow {
        emit(ResultState.Loading)
        surveysCollection
            .document(survey.id.toString())
            .set(SurveyEntity.from(survey))
            .await()
        Log.d(this@FirestoreSurveyRepository.TAG, "createSurvey:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirestoreSurveyRepository.TAG, "createSurvey:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(Dispatchers.IO)
}