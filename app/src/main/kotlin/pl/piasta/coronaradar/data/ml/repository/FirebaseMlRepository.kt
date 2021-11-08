package pl.piasta.coronaradar.data.ml.repository

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModel
import com.google.mlkit.common.model.RemoteModelManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.TAG
import javax.inject.Inject

class FirebaseMlRepository @Inject constructor(
    private val remoteModelManager: RemoteModelManager,
    private val remoteModel: RemoteModel
) : MlRepository {

    override fun isModelDownloaded(): Flow<ResultState<Boolean>> = flow {
        emit(ResultState.Loading)
        val result = remoteModelManager.isModelDownloaded(remoteModel).await()
        Log.d(this@FirebaseMlRepository.TAG, "isModelDownloaded:success")
        emit(ResultState.Success(result))
    }.catch { ex ->
        Log.w(this@FirebaseMlRepository.TAG, "isModelDownloaded:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(Dispatchers.IO)

    override fun downloadModel(isWiFiRequired: Boolean): Flow<ResultState<Nothing>> = flow {
        emit(ResultState.Loading)
        remoteModelManager.download(
            remoteModel,
            DownloadConditions.Builder().apply {
                requireWifi().takeIf { isWiFiRequired }
            }.build()
        ).await()
        Log.d(this@FirebaseMlRepository.TAG, "downloadModel:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirebaseMlRepository.TAG, "downloadModel:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(Dispatchers.IO)
}