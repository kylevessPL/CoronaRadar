package pl.piasta.coronaradar.ui.radar.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Classification(val result: ClassificationResult, val probability: Int) : Parcelable

enum class ClassificationResult {

    POSITIVE, NEGATIVE;
}