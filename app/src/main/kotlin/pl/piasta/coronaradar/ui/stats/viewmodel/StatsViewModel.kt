package pl.piasta.coronaradar.ui.stats.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) :
    ViewModel()