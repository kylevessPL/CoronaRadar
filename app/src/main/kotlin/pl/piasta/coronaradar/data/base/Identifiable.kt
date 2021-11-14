package pl.piasta.coronaradar.data.base

import java.io.Serializable

interface Identifiable<TKey : Serializable> {
    fun toModel(id: TKey): Any
}