package pl.piasta.coronaradar.data.survey.repository.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import pl.piasta.coronaradar.data.base.BaseEntity
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.data.survey.model.SurveyDetails
import pl.piasta.coronaradar.util.NoArg
import java.util.*

@NoArg
data class SurveyEntity(
    @ServerTimestamp var date: Timestamp?,
    var details: Map<String, Any>?
) : BaseEntity<String> {

    companion object {
        fun from(survey: Survey) = with(survey) {
            SurveyEntity(Timestamp(Date.from(date)), details.asMap)
        }
    }

    override fun toModel(id: String) = Survey(
        UUID.fromString(id),
        date!!.toDate().toInstant(),
        SurveyDetails.from(details!!)
    )
}