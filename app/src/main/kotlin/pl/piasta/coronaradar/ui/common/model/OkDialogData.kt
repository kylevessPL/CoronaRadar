package pl.piasta.coronaradar.ui.common.model

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class OkDialogData(
    @StringRes val titleRes: Int,
    @StringRes val messageRes: Int,
    val positiveButtonAction: () -> Unit = {},
    @StringRes val positiveButtonRes: Int = android.R.string.ok,
    val hasCancelButton: Boolean = false
) : Parcelable