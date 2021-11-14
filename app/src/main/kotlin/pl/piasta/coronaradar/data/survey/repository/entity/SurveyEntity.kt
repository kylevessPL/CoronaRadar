package pl.piasta.coronaradar.data.survey.repository.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import pl.piasta.coronaradar.data.base.BaseEntity
import pl.piasta.coronaradar.data.common.ResultLabel
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.data.survey.model.SurveyDetails
import pl.piasta.coronaradar.util.NoArg
import java.util.Date
import java.util.UUID

@NoArg
data class SurveyEntity(
    @ServerTimestamp var fillDate: Timestamp?,
    var result: String?,
    var probability: Int?,
    var details: Map<String, Any>
) : BaseEntity<String> {

    companion object {
        fun from(survey: Survey) = with(survey) {
            SurveyEntity(Timestamp(Date.from(fillDate)), result.name, probability, details.asMap)
        }
    }

    override fun toModel(id: String) = Survey(
        UUID.fromString(id),
        fillDate!!.toDate().toInstant(),
        ResultLabel.valueOf(result!!),
        probability!!,
        SurveyDetails.from(details)
    )
}