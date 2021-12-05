package pl.piasta.coronaradar.data.survey.repository.entity

import pl.piasta.coronaradar.data.base.BaseEntity.StatisticsEntity
import pl.piasta.coronaradar.data.survey.model.CountryStatistics
import pl.piasta.coronaradar.util.NoArg

@NoArg
data class CountryStatisticsEntity(
    override var negative: Map<String, Long>?,
    override var positive: Map<String, Long>?
) : StatisticsEntity<String> {

    companion object {
        @JvmStatic
        fun from(countryStatistics: CountryStatistics) = with(countryStatistics) {
            CountryStatisticsEntity(negative, positive)
        }
    }

    override fun toModel(id: String) = CountryStatistics(negative!!, positive!!)
}