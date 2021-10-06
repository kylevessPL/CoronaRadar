package pl.piasta.coronaradar.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.piasta.coronaradar.ui.common.SingleLiveEvent

abstract class BaseViewModel : ViewModel() {

    val message by lazy { SingleLiveEvent<String>() }
    val isLoading by lazy { MutableLiveData<Boolean>().apply { value = false } }
}