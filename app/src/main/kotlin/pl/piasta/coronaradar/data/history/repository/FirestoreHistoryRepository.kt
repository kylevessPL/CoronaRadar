package pl.piasta.coronaradar.data.history.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import pl.piasta.coronaradar.data.common.FirestorePagingSource
import pl.piasta.coronaradar.data.history.model.History
import pl.piasta.coronaradar.data.history.repository.entity.HistoryEntity
import pl.piasta.coronaradar.data.util.HISTORY
import pl.piasta.coronaradar.di.GetAllHistoryQuery
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.TAG

class FirestoreHistoryRepository(
    private val firestore: FirebaseFirestore,
    private val pagingConfig: PagingConfig,
    @GetAllHistoryQuery private val getAllHistoryQuery: Query
) : HistoryRepository {

    override fun getAllHistoryPaged() = Pager(pagingConfig) {
        FirestorePagingSource<History>(getAllHistoryQuery, HistoryEntity::class.java)
    }.flow.flowOn(Dispatchers.IO)

    override fun createHistory(history: History): Flow<ResultState<Nothing>> = flow {
        emit(ResultState.Loading)
        firestore
            .collection(HISTORY)
            .document(history.id.toString())
            .set(HistoryEntity.from(history))
            .await()
        Log.d(this@FirestoreHistoryRepository.TAG, "createHistory:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirestoreHistoryRepository.TAG, "createHistory:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(Dispatchers.IO)
}