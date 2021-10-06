package pl.piasta.coronaradar.ui.main

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.piasta.coronaradar.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel()