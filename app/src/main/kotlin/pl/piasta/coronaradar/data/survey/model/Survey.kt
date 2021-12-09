package pl.piasta.coronaradar.data.survey.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromMap
import kotlinx.serialization.properties.encodeToMap
import pl.piasta.coronaradar.data.common.*
import java.time.Instant
import java.util.*

@Parcelize
data class Survey(
    val id: UUID,
    val date: Instant,
    val details: SurveyDetails
) : Parcelable

@Serializable
@Parcelize
data class SurveyDetails(
    val result: ResultLabel,
    val probability: Long,
    val name: String,
    val age: Long,
    val gender: Gender,
    val continent: Continent,
    val illnesses: List<CommonIllness> = emptyList(),
    val inQuarantine: Boolean,
    val closeContact: Boolean,
    val travelAbroad: Boolean,
    val smoker: Boolean,
    val symptoms: List<CommonSymptom> = emptyList(),
    val wellbeingScale: Long
) : Parcelable {

    @IgnoredOnParcel
    val asMap: Map<String, Any> by lazy { Properties.encodeToMap(this) }

    companion object {
        @JvmStatic
        fun from(map: Map<String, Any>): SurveyDetails = Properties.decodeFromMap(map)
    }
}