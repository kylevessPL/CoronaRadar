package pl.piasta.coronaradar.data.history.repository

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
import pl.piasta.coronaradar.data.history.model.History
import pl.piasta.coronaradar.data.history.repository.entity.HistoryEntity
import pl.piasta.coronaradar.di.GetAllUserHistoryPagingQuery
import pl.piasta.coronaradar.di.UserHistoryCollection
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.TAG
import javax.inject.Inject

class FirestoreHistoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val pagingConfig: PagingConfig,
    @UserHistoryCollection private val userHistoryCollection: CollectionReference,
    @GetAllUserHistoryPagingQuery private val getAllUserHistoryPagingQuery: Query
) : HistoryRepository {

    override fun getAllCurrentUserHistoryPaged() = Pager(pagingConfig) {
        FirestorePagingSource<History>(getAllUserHistoryPagingQuery, HistoryEntity::class.java)
    }.flow.flowOn(Dispatchers.IO)

    override fun createHistory(history: History): Flow<ResultState<Nothing>> = flow {
        emit(ResultState.Loading)
        userHistoryCollection
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