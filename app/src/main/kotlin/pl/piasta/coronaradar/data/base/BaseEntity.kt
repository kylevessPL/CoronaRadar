package pl.piasta.coronaradar.data.base

import java.io.Serializable

interface BaseEntity<TKey : Serializable> {
    fun toModel(id: TKey): Any

    interface StatisticsEntity<TKey : Serializable> : BaseEntity<TKey> {
        var negative: Map<String, Long>?
        var positive: Map<String, Long>?
    }
}