package pl.piasta.coronaradar.data.history.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromMap
import kotlinx.serialization.properties.encodeToMap
import pl.piasta.coronaradar.data.common.ResultLabel
import java.time.Instant
import java.util.*

@Parcelize
data class History(
    val id: UUID,
    val date: Instant,
    val details: HistoryDetails
) : Parcelable

@Serializable
@Parcelize
data class HistoryDetails(
    val result: ResultLabel,
    val probability: Long
) : Parcelable {

    @IgnoredOnParcel
    val asMap: Map<String, Any> by lazy { Properties.encodeToMap(this) }

    companion object {
        @JvmStatic
        fun from(map: Map<String, Any>): HistoryDetails = Properties.decodeFromMap(map)
    }
}