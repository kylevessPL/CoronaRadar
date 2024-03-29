package pl.piasta.coronaradar.data.common

import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.base.Labellable
import pl.piasta.coronaradar.data.base.LabelledEnum

enum class ResultLabel(override val label: Int) : Labellable {
    NEGATIVE(R.string.negative),
    POSITIVE(R.string.positive);

    companion object : LabelledEnum<ResultLabel>
}