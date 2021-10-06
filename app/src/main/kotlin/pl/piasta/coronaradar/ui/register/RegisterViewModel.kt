package pl.piasta.coronaradar.ui.register

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.piasta.coronaradar.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) :
    BaseViewModel()