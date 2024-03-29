package pl.piasta.coronaradar.ui.common.view

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.ui.common.model.OkDialogData
import splitties.alertdialog.appcompat.cancelButton
import splitties.alertdialog.appcompat.messageResource
import splitties.alertdialog.appcompat.positiveButton
import splitties.alertdialog.appcompat.titleResource
import splitties.alertdialog.material.materialAlertDialog

@AndroidEntryPoint
class OkDialogFragment : DialogFragment() {

    companion object {
        const val DATA = "data"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val data = requireArguments().getParcelable<OkDialogData>(DATA)!!
        return requireContext().materialAlertDialog {
            titleResource = data.titleRes
            messageResource = data.messageRes
            positiveButton(data.positiveButtonRes) { data.positiveButtonAction() }
            cancelButton().takeIf { data.hasCancelButton }
        }
    }
}