package pl.piasta.coronaradar.util

import androidx.annotation.Keep

sealed class ResultState<out R> {

    @Keep
    data class Success<out T>(val data: T? = null) : ResultState<T>()
    data class Error(val ex: Throwable) : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data=$data]"
            is Error -> "Error[exception=$ex]"
            is Loading -> "Loading"
        }
    }
}
