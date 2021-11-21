package pl.piasta.coronaradar.data.survey.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromMap
import kotlinx.serialization.properties.encodeToMap
import pl.piasta.coronaradar.data.common.*
import java.time.Instant
import java.util.*

data class Survey(
    val id: UUID,
    val date: Instant,
    val details: SurveyDetails
)

@Serializable
data class SurveyDetails(
    val result: ResultLabel,
    val probability: Long,
    val ageRange: AgeRange,
    val gender: Gender,
    val country: String,
    val illnesses: List<CommonIllness>,
    val inQuarantine: Boolean,
    val closeContact: Boolean,
    val travelAbroad: Boolean,
    val smoker: Boolean,
    val symptoms: List<CommonSymptom>,
    val wellbeingScale: Long
) {

    val asMap: Map<String, Any> by lazy { Properties.encodeToMap(this) }

    companion object {
        @JvmStatic
        fun from(map: Map<String, Any>): SurveyDetails = Properties.decodeFromMap(map)
    }
}