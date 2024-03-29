package pl.piasta.coronaradar.data.ml.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType.LATEST_MODEL
import com.google.firebase.ml.modeldownloader.DownloadType.LOCAL_MODEL
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import pl.piasta.coronaradar.BuildConfig.*
import pl.piasta.coronaradar.di.IoDispatcher
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.TAG
import javax.inject.Inject

class FirebaseMlRepository @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val modelDownloader: FirebaseModelDownloader
) : MlRepository {

    override fun downloadModel(isWiFiRequired: Boolean) = flow {
        emit(ResultState.Loading)
        val conditions = CustomModelDownloadConditions.Builder().apply {
            requireWifi().takeIf { isWiFiRequired }
        }.build()
        modelDownloader.getModel(
            ML_MODEL_NAME,
            LATEST_MODEL,
            conditions
        ).await()
        Log.d(this@FirebaseMlRepository.TAG, "downloadModel:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirebaseMlRepository.TAG, "downloadModel:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(coroutineDispatcher)

    override suspend fun getLocalModel() = runCatching {
        val conditions = CustomModelDownloadConditions.Builder().build()
        val model = modelDownloader.getModel(
            ML_MODEL_NAME,
            LOCAL_MODEL,
            conditions
        ).await()
        Uri.parse(model.localFilePath)
    }.getOrNull()
}