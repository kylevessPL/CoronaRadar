package pl.piasta.coronaradar.data.survey.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.DESCENDING
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import pl.piasta.coronaradar.data.common.FirestorePagingSource
import pl.piasta.coronaradar.data.common.ResultLabel
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.data.survey.repository.entity.SurveyEntity
import pl.piasta.coronaradar.data.util.DATE
import pl.piasta.coronaradar.data.util.PAGE_SIZE
import pl.piasta.coronaradar.data.util.SURVEYS
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.TAG
import java.util.*
import javax.inject.Inject

class FirestoreSurveyRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val pagingConfig: PagingConfig
) : SurveyRepository {

    private val surveysCollection by lazy {
        firestore.collection(SURVEYS)
    }

    private val getAllSurveysPagingQuery by lazy {
        firestore
            .collection(SURVEYS)
            .orderBy(DATE, DESCENDING)
            .limit(PAGE_SIZE.toLong())
    }

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

    override fun uploadCoughAudio(label: ResultLabel, byteArray: ByteArray) = flow {
        emit(ResultState.Loading)
        val ref =
            storage.reference.child("cough/${label.name.lowercase()}/${UUID.randomUUID()}.wav")
        ref.putBytes(byteArray).await()
        Log.d(this@FirestoreSurveyRepository.TAG, "uploadCoughAudio:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirestoreSurveyRepository.TAG, "uploadCoughAudio:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(Dispatchers.IO)
}