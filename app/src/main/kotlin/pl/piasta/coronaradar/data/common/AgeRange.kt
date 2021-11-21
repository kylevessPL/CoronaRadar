package pl.piasta.coronaradar.data.common

import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.base.Labellable
import pl.piasta.coronaradar.data.base.LabelledEnum

enum class AgeRange(override val label: Int) : Labellable {
    RANGE_18_MINUS(R.string.range_18_minus),
    RANGE_18_24(R.string.range_18_24),
    RANGE_25_34(R.string.range_25_34),
    RANGE_35_44(R.string.range_35_44),
    RANGE_45_54(R.string.range_45_54),
    RANGE_55_64(R.string.range_55_64),
    RANGE_65_PLUS(R.string.range_65_plus);

    companion object : LabelledEnum<AgeRange>
}