package pl.piasta.coronaradar.data.base

import java.io.Serializable

interface BaseEntity<TKey : Serializable> {
    fun toModel(id: TKey): Any
}