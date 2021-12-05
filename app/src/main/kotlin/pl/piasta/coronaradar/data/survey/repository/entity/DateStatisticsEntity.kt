package pl.piasta.coronaradar.data.survey.repository.entity

import pl.piasta.coronaradar.data.base.BaseEntity.StatisticsEntity
import pl.piasta.coronaradar.data.survey.model.DateStatistics
import pl.piasta.coronaradar.util.NoArg
import java.time.YearMonth

@NoArg
data class DateStatisticsEntity(
    override var negative: Map<String, Long>?,
    override var positive: Map<String, Long>?
) : StatisticsEntity<String> {

    companion object {
        @JvmStatic
        fun from(dateStatistics: DateStatistics) = with(dateStatistics) {
            DateStatisticsEntity(
                negative.mapKeys { it.key.toString() },
                positive.mapKeys { it.key.toString() })
        }
    }

    override fun toModel(id: String) = DateStatistics(
        negative!!.mapKeys { YearMonth.parse(it.key) },
        positive!!.mapKeys { YearMonth.parse(it.key) }
    )
}