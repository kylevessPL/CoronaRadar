package pl.piasta.coronaradar.data.common

import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.base.Labellable
import pl.piasta.coronaradar.data.base.LabelledEnum

enum class Continent(override val label: Int) : Labellable {
    NORTH_AMERICA(R.string.north_america),
    SOUTH_AMERICA(R.string.south_america),
    AFRICA(R.string.africa),
    ASIA(R.string.asia),
    EUROPE(R.string.europe),
    ANTARTICA(R.string.antartica),
    AUSTRALIA(R.string.australia);

    companion object : LabelledEnum<Continent>
}