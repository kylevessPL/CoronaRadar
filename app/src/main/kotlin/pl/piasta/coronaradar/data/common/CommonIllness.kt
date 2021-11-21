package pl.piasta.coronaradar.data.common

import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.base.Labellable
import pl.piasta.coronaradar.data.base.LabelledEnum

enum class CommonIllness(override val label: Int) : Labellable {
    CANCER(R.string.cancer),
    CHRONIC_LUNG_DISEASE(R.string.chronic_lung_disease),
    CHRONIC_LIVER_DISEASE(R.string.chronic_liver_disease),
    DIABETES_MELLITUS(R.string.diabetes_mellitus),
    HEART_CONDITIONS(R.string.heart_conditions),
    ASTHMA(R.string.asthma),
    HYPERTENSION(R.string.hypertension);

    companion object : LabelledEnum<CommonIllness>
}