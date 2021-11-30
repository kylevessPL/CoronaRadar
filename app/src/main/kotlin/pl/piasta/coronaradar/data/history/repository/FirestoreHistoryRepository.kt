package pl.piasta.coronaradar.data.history.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.DESCENDING
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import pl.piasta.coronaradar.data.common.FirestorePagingSource
import pl.piasta.coronaradar.data.history.model.History
import pl.piasta.coronaradar.data.history.repository.entity.HistoryEntity
import pl.piasta.coronaradar.data.util.DATE
import pl.piasta.coronaradar.data.util.HISTORY
import pl.piasta.coronaradar.data.util.PAGE_SIZE
import pl.piasta.coronaradar.data.util.USERS
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.TAG
import javax.inject.Inject

class FirestoreHistoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val pagingConfig: PagingConfig
) : HistoryRepository {

    private val userHistoryCollection by lazy {
        firestore
            .collection(USERS)
            .document(Firebase.auth.uid.toString())
            .collection(HISTORY)
    }

    private val getAllUserHistoryPagingQuery by lazy {
        firestore
            .collection(USERS)
            .document(Firebase.auth.uid.toString())
            .collection(HISTORY)
            .orderBy(DATE, DESCENDING)
            .limit(PAGE_SIZE.toLong())
    }

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