package pl.piasta.coronaradar.ui.radar.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import pl.piasta.coronaradar.data.common.ResultLabel

@Parcelize
data class Classification(val result: ResultLabel, val probability: Int) : Parcelable