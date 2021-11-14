package pl.piasta.coronaradar.data.survey.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromMap
import kotlinx.serialization.properties.encodeToMap
import pl.piasta.coronaradar.data.common.ResultLabel
import java.time.Instant
import java.util.UUID

data class Survey(
    val id: UUID,
    val fillDate: Instant,
    val result: ResultLabel,
    val probability: Int,
    val details: SurveyDetails
)

@Serializable
data class SurveyDetails(
    val age: Int,
    val gender: Gender,
    val country: String,
    val illnesses: List<String>,
    val temperature: Float,
    val inQuarantine: Boolean,
    val closeContact: Boolean,
    val travelAbroad: Boolean,
    val diarrhoea: Boolean,
    val breathShortness: Boolean,
    val tiredness: Boolean,
    val smellLoss: Boolean,
    val rash: Boolean
) {

    val asMap: Map<String, Any> by lazy { Properties.encodeToMap(this) }

    companion object {
        @JvmStatic
        fun from(map: Map<String, Any>): SurveyDetails = Properties.decodeFromMap(map)
    }
}

enum class Gender {
    MALE, FEMALE
}