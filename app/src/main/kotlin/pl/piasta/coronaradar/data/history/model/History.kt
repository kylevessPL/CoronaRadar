package pl.piasta.coronaradar.data.history.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromMap
import kotlinx.serialization.properties.encodeToMap
import pl.piasta.coronaradar.data.common.ResultLabel
import java.time.Instant
import java.util.*

data class History(
    val id: UUID,
    val date: Instant,
    val details: HistoryDetails
)

@Serializable
data class HistoryDetails(
    val result: ResultLabel,
    val probability: Long
) {

    val asMap: Map<String, Any> by lazy { Properties.encodeToMap(this) }

    companion object {
        @JvmStatic
        fun from(map: Map<String, Any>): HistoryDetails = Properties.decodeFromMap(map)
    }
}