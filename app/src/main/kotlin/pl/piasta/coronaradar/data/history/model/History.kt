package pl.piasta.coronaradar.data.history.model

import pl.piasta.coronaradar.data.common.ResultLabel
import java.time.Instant
import java.util.UUID

data class History(
    val id: UUID,
    val analysisDate: Instant,
    val result: ResultLabel,
    val probability: Float
)