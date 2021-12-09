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
class StatsViewModel @Inject constructor(surveyRepository: SurveyRepository) : ViewModel() {

    private val _surveysData = surveyRepository.getAllSurveysPaged()
        .cachedIn(viewModelScope)
        .asLiveData()
    val surveysData: LiveData<PagingData<Survey>>
        get() = _surveysData

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing

    private val _emptyPlaceholderVisibility = MutableLiveData(false)
    val emptyPlaceholderVisibility: LiveData<Boolean>
        get() = _emptyPlaceholderVisibility

    private val _displaySurveyDialog = LiveEvent<Survey>()
    val displaySurveyDialog: LiveData<Survey>
        get() = _displaySurveyDialog

    private val _surveyDetailsDialogDismiss = LiveEvent<Boolean>()
    val surveyDetailsDialogDismiss: LiveData<Boolean>
        get() = _surveyDetailsDialogDismiss

    private val _displayIllnessDetails = LiveEvent<Boolean>()
    val displayIllnessDetails: LiveData<Boolean>
        get() = _displayIllnessDetails

    private val _refreshData = LiveEvent<Boolean>()
    val refreshData: LiveData<Boolean>
        get() = _refreshData

    fun setDataRefreshing(value: Boolean) {
        _isRefreshing.value = value
    }

    fun setEmptyPlaceholderVisibility(visible: Boolean) {
        _emptyPlaceholderVisibility.value = visible
    }

    fun displayIllnessDetailsEvent(value: Boolean) {
        _displayIllnessDetails.value = value
    }

    fun refreshDataEvent() {
        _refreshData.value = true
    }

    fun displaySurveyDialogEvent(survey: Survey) {
        _displaySurveyDialog.value = survey
    }

    fun surveyDetailsDialogDismissEvent() {
        _surveyDetailsDialogDismiss.value = true
    }
}