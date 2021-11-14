package pl.piasta.coronaradar.data.common

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import pl.piasta.coronaradar.data.base.BaseEntity
import pl.piasta.coronaradar.util.TAG

@Suppress("UNCHECKED_CAST")
class FirestorePagingSource<T : Any>(
    private val query: Query,
    private val entityClazz: Class<out BaseEntity<String>>
) : PagingSource<QuerySnapshot, T>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, T>): QuerySnapshot? = null

    override suspend fun load(params: LoadParams<QuerySnapshot>) = runCatching {
        val currentPage = params.key ?: query.get().await()
        val lastVisible = currentPage.documents[currentPage.size() - 1]
        val nextPage = query.startAfter(lastVisible).get().await()
        val data = currentPage.documents.map {
            it?.toObject(entityClazz)!!.toModel(it.id) as T
        }
        Log.d(this@FirestorePagingSource.TAG, "load:success ".plus(entityClazz.simpleName))
        LoadResult.Page(data, null, nextPage)
    }.getOrElse { ex ->
        Log.w(this@FirestorePagingSource.TAG, "load:failure ".plus(entityClazz.simpleName), ex)
        LoadResult.Error(ex)
    }
}