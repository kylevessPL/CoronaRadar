package pl.piasta.coronaradar.ui.history.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.piasta.coronaradar.data.history.model.History
import pl.piasta.coronaradar.data.history.repository.HistoryRepository
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(historyRepository: HistoryRepository) : ViewModel() {

    private val _historyData = historyRepository.getAllCurrentUserHistoryPaged()
        .cachedIn(viewModelScope)
        .asLiveData()
    val historyData: LiveData<PagingData<History>>
        get() = _historyData

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing

    private val _emptyPlaceholderVisibility = MutableLiveData(false)
    val emptyPlaceholderVisibility: LiveData<Boolean>
        get() = _emptyPlaceholderVisibility

    private val _refreshData = LiveEvent<Boolean>()
    val refreshData: LiveData<Boolean>
        get() = _refreshData

    fun setDataRefreshing(value: Boolean) {
        _isRefreshing.value = value
    }

    fun setEmptyPlaceholderVisibility(visible: Boolean) {
        _emptyPlaceholderVisibility.value = visible
    }

    fun refreshDataEvent() {
        _refreshData.value = true
    }
}