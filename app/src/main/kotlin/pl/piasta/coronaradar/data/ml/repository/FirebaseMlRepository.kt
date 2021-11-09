package pl.piasta.coronaradar.data.ml.repository

import android.graphics.BitmapFactory
import android.util.Log
import com.google.mlkit.common.model.CustomRemoteModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import pl.piasta.coronaradar.data.ml.model.Classification
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.TAG
import javax.inject.Inject

class FirebaseMlRepository @Inject constructor(
    private val remoteModelManager: RemoteModelManager,
    private val remoteModel: CustomRemoteModel,
    private val imageLabeler: ImageLabeler
) : MlRepository {

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

    override fun classify(byteArray: ByteArray): Flow<ResultState<Classification>> = flow {
        emit(ResultState.Loading)
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        val image = InputImage.fromBitmap(bitmap, 0)
        val result = imageLabeler.process(image).await()
        val classification = with(result.first()) { Classification(text, confidence) }
        Log.d(this@FirebaseMlRepository.TAG, "classify:success")
        emit(ResultState.Success(classification))
    }.catch { ex ->
        Log.w(this@FirebaseMlRepository.TAG, "classify:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(Dispatchers.IO)
}