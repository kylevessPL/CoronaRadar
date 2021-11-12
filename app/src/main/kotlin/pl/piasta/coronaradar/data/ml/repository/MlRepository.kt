package pl.piasta.coronaradar.data.ml.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import pl.piasta.coronaradar.util.ResultState

interface MlRepository {

    fun downloadModel(isWiFiRequired: Boolean): Flow<ResultState<Nothing>>
    suspend fun getLocalModel(): Uri?
}