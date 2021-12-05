package pl.piasta.coronaradar.data.common

import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.base.Labellable
import pl.piasta.coronaradar.data.base.LabelledEnum

enum class AgeRange(override val label: Int) : Labellable {
    AGE_RANGE_18_MINUS(R.string.range_18_minus),
    AGE_RANGE_18_24(R.string.range_18_24),
    AGE_RANGE_25_34(R.string.range_25_34),
    AGE_RANGE_35_44(R.string.range_35_44),
    AGE_RANGE_45_54(R.string.range_45_54),
    AGE_RANGE_55_64(R.string.range_55_64),
    AGE_RANGE_65_PLUS(R.string.range_65_plus);

    fun asIntRange() = when (this) {
        AGE_RANGE_18_MINUS -> IntRange(0, 17)
        AGE_RANGE_18_24 -> IntRange(18, 24)
        AGE_RANGE_25_34 -> IntRange(25, 34)
        AGE_RANGE_35_44 -> IntRange(35, 44)
        AGE_RANGE_45_54 -> IntRange(45, 54)
        AGE_RANGE_55_64 -> IntRange(55, 64)
        AGE_RANGE_65_PLUS -> IntRange(65, 150)
    }

    companion object : LabelledEnum<AgeRange> {
        @JvmStatic
        fun fromAge(age: Int) = enumValues<AgeRange>().find {
            it.asIntRange().contains(age)
        }
    }
}