package pl.piasta.coronaradar.data.history.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pl.piasta.coronaradar.data.history.model.History
import pl.piasta.coronaradar.util.ResultState

interface HistoryRepository {

    fun getAllCurrentUserHistoryPaged(): Flow<PagingData<History>>
    fun createHistory(history: History): Flow<ResultState<Nothing>>
}