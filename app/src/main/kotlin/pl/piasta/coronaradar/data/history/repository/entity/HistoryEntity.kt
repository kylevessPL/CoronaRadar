package pl.piasta.coronaradar.data.history.repository.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import pl.piasta.coronaradar.data.base.BaseEntity
import pl.piasta.coronaradar.data.common.ResultLabel
import pl.piasta.coronaradar.data.history.model.History
import pl.piasta.coronaradar.util.NoArg
import java.util.Date
import java.util.UUID

@NoArg
data class HistoryEntity(
    @ServerTimestamp var analysisDate: Timestamp?,
    var result: String?,
    var probability: Float?
) : BaseEntity<String> {

    companion object {
        fun from(history: History) = with(history) {
            HistoryEntity(Timestamp(Date.from(analysisDate)), result.name, probability)
        }
    }

    override fun toModel(id: String) = History(
        UUID.fromString(id),
        analysisDate!!.toDate().toInstant(),
        ResultLabel.valueOf(result!!),
        probability!!
    )
}