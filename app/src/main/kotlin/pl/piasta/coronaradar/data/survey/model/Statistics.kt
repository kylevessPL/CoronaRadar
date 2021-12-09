package pl.piasta.coronaradar.data.survey.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import pl.piasta.coronaradar.data.common.AgeRange
import pl.piasta.coronaradar.data.common.Continent
import pl.piasta.coronaradar.data.common.Gender
import java.time.YearMonth

@Parcelize
data class Statistics(
    val ageStatistics: AgeStatistics,
    val continentStatistics: ContinentStatistics,
    val dateStatistics: DateStatistics,
    val genderStatistics: GenderStatistics
) : Parcelable

@Parcelize
data class AgeStatistics(
    val negative: Map<AgeRange, Long>,
    val positive: Map<AgeRange, Long>
) : Parcelable

@Parcelize
data class ContinentStatistics(
    val negative: Map<Continent, Long>,
    val positive: Map<Continent, Long>
) : Parcelable

@Parcelize
data class DateStatistics(
    val negative: Map<YearMonth, Long>,
    val positive: Map<YearMonth, Long>
) : Parcelable

@Parcelize
data class GenderStatistics(
    val negative: Map<Gender, Long>,
    val positive: Map<Gender, Long>
) : Parcelable