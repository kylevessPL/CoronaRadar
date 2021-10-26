package pl.piasta.coronaradar.ui.common.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OkDialogData(
    val title: String,
    val message: String,
    val positiveAction: () -> Unit? = {}
) : Parcelable