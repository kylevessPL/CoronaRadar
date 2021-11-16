package pl.piasta.coronaradar.ui.stats.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.data.survey.repository.SurveyRepository
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    surveyRepository: SurveyRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _surveysData = surveyRepository.getAllSurveysPaged()
        .cachedIn(viewModelScope)
        .asLiveData()
    val surveysData: LiveData<PagingData<Survey>>
        get() = _surveysData

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing

    private val _refreshData = LiveEvent<Boolean>()
    val refreshData: LiveData<Boolean>
        get() = _refreshData

    fun setDataRefreshing(value: Boolean) {
        _isRefreshing.value = value
    }

    fun refreshDataEvent() {
        _refreshData.value = true
    }

    fun handleItemClicked(item: Survey) {
    }
}