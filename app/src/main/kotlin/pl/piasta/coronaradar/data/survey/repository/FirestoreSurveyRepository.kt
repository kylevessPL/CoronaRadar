package pl.piasta.coronaradar.data.survey.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.DESCENDING
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import pl.piasta.coronaradar.data.base.BaseEntity.StatisticsEntity
import pl.piasta.coronaradar.data.common.AgeRange
import pl.piasta.coronaradar.data.common.FirestorePagingSource
import pl.piasta.coronaradar.data.common.ResultLabel
import pl.piasta.coronaradar.data.common.ResultLabel.NEGATIVE
import pl.piasta.coronaradar.data.common.ResultLabel.POSITIVE
import pl.piasta.coronaradar.data.survey.model.Statistics
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.data.survey.repository.entity.*
import pl.piasta.coronaradar.data.util.*
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.TAG
import pl.piasta.coronaradar.util.getDataFlow
import java.time.YearMonth
import java.time.ZoneOffset.UTC
import java.util.*
import javax.inject.Inject

class FirestoreSurveyRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val pagingConfig: PagingConfig
) : SurveyRepository {

    private val statisticsCollection by lazy {
        firestore.collection(STATISTICS)
    }

    private val surveysCollection by lazy {
        firestore.collection(SURVEYS)
    }

    private val getAllSurveysPagingQuery by lazy {
        firestore
            .collection(SURVEYS)
            .orderBy(DATE, DESCENDING)
            .limit(PAGE_SIZE.toLong())
    }

    override fun watchStatistics(): Flow<Statistics> = statisticsCollection.getDataFlow {
        createStatistics(it!!.documents)
    }.flowOn(Dispatchers.IO)

    override fun getAllSurveysPaged() = Pager(pagingConfig) {
        FirestorePagingSource<Survey>(getAllSurveysPagingQuery, SurveyEntity::class.java)
    }.flow.flowOn(Dispatchers.IO)

    override fun createSurvey(survey: Survey): Flow<ResultState<Nothing>> = flow {
        emit(ResultState.Loading)
        firestore.runTransaction { transaction ->
            val ageStatistics = transaction.get(statisticsCollection.document(AGE))
                .toObject<AgeStatisticsEntity>()!!.also {
                    updateAgeStatistics(survey, it)
                }
            val countryStatistics = transaction.get(statisticsCollection.document(COUNTRY))
                .toObject<CountryStatisticsEntity>()!!.also {
                    updateCountryStatistics(survey, it)
                }
            val dateStatistics = transaction.get(statisticsCollection.document(DATE))
                .toObject<DateStatisticsEntity>()!!.also {
                    updateDateStatistics(survey, it)
                }
            val genderStatistics = transaction.get(statisticsCollection.document(GENDER))
                .toObject<GenderStatisticsEntity>()!!.also {
                    updateGenderStatistics(survey, it)
                }
            transaction.set(
                statisticsCollection.document(AGE),
                ageStatistics,
                SetOptions.merge()
            )
            transaction.set(
                statisticsCollection.document(COUNTRY),
                countryStatistics,
                SetOptions.merge()
            )
            transaction.set(
                statisticsCollection.document(DATE),
                dateStatistics,
                SetOptions.merge()
            )
            transaction.set(
                statisticsCollection.document(GENDER),
                genderStatistics,
                SetOptions.merge()
            )
            transaction.set(
                surveysCollection.document(survey.id.toString()),
                SurveyEntity.from(survey)
            )
        }.await()
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

    private fun createStatistics(documents: List<DocumentSnapshot>) = Statistics(
        documents.find { it.id == AGE }?.toObject()!!,
        documents.find { it.id == COUNTRY }?.toObject()!!,
        documents.find { it.id == DATE }?.toObject()!!,
        documents.find { it.id == GENDER }?.toObject()!!
    )

    private fun updateAgeStatistics(survey: Survey, ageStatistics: AgeStatisticsEntity) =
        ageStatistics.apply {
            val result = survey.details.result
            val age = AgeRange.fromAge(survey.details.age.toInt())!!
            val map = getStatisticsMapByResult(result, ageStatistics)!!
            val current = map.getOrPut(age.name, { 0L })
            map[age.name] = current.plus(1)
            updateStatisticsMapByResult(result, ageStatistics, map)
        }

    private fun updateCountryStatistics(
        survey: Survey,
        countryStatistics: CountryStatisticsEntity
    ) {
        val result = survey.details.result
        val country = survey.details.country.replaceFirstChar { it.uppercaseChar() }
        val map = getStatisticsMapByResult(result, countryStatistics)!!
        val current = map.getOrPut(country, { 0L })
        map[country] = current.plus(1)
        updateStatisticsMapByResult(result, countryStatistics, map)
    }

    private fun updateDateStatistics(survey: Survey, dateStatistics: DateStatisticsEntity) {
        val result = survey.details.result
        val date = YearMonth.from(survey.date.atOffset(UTC)).toString()
        val map = getStatisticsMapByResult(result, dateStatistics)!!
        val current = map.getOrPut(date, { 0L })
        map[date] = current.plus(1)
        updateStatisticsMapByResult(result, dateStatistics, map)
    }

    private fun updateGenderStatistics(survey: Survey, genderStatistics: GenderStatisticsEntity) {
        val result = survey.details.result
        val gender = survey.details.gender.name
        val map = getStatisticsMapByResult(result, genderStatistics)!!
        val current = map.getOrPut(gender, { 0L })
        map[gender] = current.plus(1)
        updateStatisticsMapByResult(result, genderStatistics, map)
    }

    private fun getStatisticsMapByResult(
        result: ResultLabel,
        statistics: StatisticsEntity<String>
    ) = when (result) {
        NEGATIVE -> statistics.negative?.toMutableMap()
        POSITIVE -> statistics.positive?.toMutableMap()
    }

    private fun updateStatisticsMapByResult(
        result: ResultLabel,
        statistics: StatisticsEntity<String>,
        updatedMap: Map<String, Long>
    ) = result.takeIf { it == NEGATIVE }?.let {
        statistics.negative = updatedMap
    } ?: run {
        statistics.positive = updatedMap
    }
}