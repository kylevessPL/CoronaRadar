package pl.piasta.coronaradar.data.common

import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.base.Labellable
import pl.piasta.coronaradar.data.base.LabelledEnum

enum class Gender(override val label: Int) : Labellable {
    MALE(R.string.male),
    FEMALE(R.string.female);

    companion object : LabelledEnum<Gender>
}