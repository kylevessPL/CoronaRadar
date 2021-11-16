package pl.piasta.coronaradar.data.history.repository.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import pl.piasta.coronaradar.data.base.BaseEntity
import pl.piasta.coronaradar.data.history.model.History
import pl.piasta.coronaradar.data.history.model.HistoryDetails
import pl.piasta.coronaradar.util.NoArg
import java.util.*

@NoArg
data class HistoryEntity(
    @ServerTimestamp var date: Timestamp?,
    var details: Map<String, Any>?
) : BaseEntity<String> {

    companion object {
        fun from(history: History) = with(history) {
            HistoryEntity(Timestamp(Date.from(date)), details.asMap)
        }
    }

    override fun toModel(id: String) = History(
        UUID.fromString(id),
        date!!.toDate().toInstant(),
        HistoryDetails.from(details!!)
    )
}