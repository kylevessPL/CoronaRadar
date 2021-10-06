package pl.piasta.coronaradar.ui.radar

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.piasta.coronaradar.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class RadarViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) :
    BaseViewModel()