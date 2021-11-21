package pl.piasta.coronaradar.data.common

import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.base.Labellable
import pl.piasta.coronaradar.data.base.LabelledEnum

enum class CommonSymptom(override val label: Int) : Labellable {
    FEVER(R.string.fever),
    DIARRHOEA(R.string.diarrhoea),
    BREATH_SHORTNESS(R.string.breath_shortness),
    TIREDNESS(R.string.tiredness),
    SMELL_LOSS(R.string.smell_loss),
    RASH(R.string.rash);

    companion object : LabelledEnum<CommonSymptom>
}