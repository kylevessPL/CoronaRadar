package pl.piasta.coronaradar.data.ml.repository

import kotlinx.coroutines.flow.Flow
import pl.piasta.coronaradar.data.ml.model.Classification
import pl.piasta.coronaradar.util.ResultState

interface MlRepository {

    fun isModelDownloaded(): Flow<ResultState<Boolean>>
    fun downloadModel(isWiFiRequired: Boolean): Flow<ResultState<Nothing>>
    fun classify(byteArray: ByteArray): Flow<ResultState<Classification>>
}