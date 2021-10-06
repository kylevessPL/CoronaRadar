package pl.piasta.coronaradar.ui.stats

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.piasta.coronaradar.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) :
    BaseViewModel()