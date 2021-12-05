package pl.piasta.coronaradar.data.survey.repository.entity

import pl.piasta.coronaradar.data.base.BaseEntity.StatisticsEntity
import pl.piasta.coronaradar.data.survey.model.AgeStatistics
import pl.piasta.coronaradar.util.NoArg

@NoArg
data class AgeStatisticsEntity(
    override var negative: Map<String, Long>?,
    override var positive: Map<String, Long>?
) : StatisticsEntity<String> {

    companion object {
        @JvmStatic
        fun from(ageStatistics: AgeStatistics) = with(ageStatistics) {
            AgeStatisticsEntity(negative.mapKeys { it.key.name }, positive.mapKeys { it.key.name })
        }
    }

    override fun toModel(id: String) = AgeStatistics(
        negative!!.mapKeys { enumValueOf(it.key) },
        positive!!.mapKeys { enumValueOf(it.key) }
    )
}