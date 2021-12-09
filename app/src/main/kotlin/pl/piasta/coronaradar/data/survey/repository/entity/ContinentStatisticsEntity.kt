package pl.piasta.coronaradar.data.survey.repository.entity

import pl.piasta.coronaradar.data.base.BaseEntity.StatisticsEntity
import pl.piasta.coronaradar.data.survey.model.ContinentStatistics
import pl.piasta.coronaradar.util.NoArg

@NoArg
data class ContinentStatisticsEntity(
    override var negative: Map<String, Long>?,
    override var positive: Map<String, Long>?
) : StatisticsEntity<String> {

    companion object {
        @JvmStatic
        fun from(continentStatistics: ContinentStatistics) = with(continentStatistics) {
            ContinentStatisticsEntity(
                negative.mapKeys { it.key.name },
                positive.mapKeys { it.key.name })
        }
    }

    override fun toModel(id: String) = ContinentStatistics(
        negative!!.mapKeys { enumValueOf(it.key) },
        positive!!.mapKeys { enumValueOf(it.key) }
    )
}