package pl.piasta.coronaradar.ui.common

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import splitties.alertdialog.appcompat.message
import splitties.alertdialog.appcompat.okButton
import splitties.alertdialog.appcompat.title
import splitties.alertdialog.material.materialAlertDialog

@AndroidEntryPoint
class OkDialog(
    private val title: String,
    private val message: String,
    private val positiveAction: () -> Unit? = {}
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireContext().materialAlertDialog {
            title = this@OkDialog.title
            message = this@OkDialog.message
            okButton { positiveAction() }
        }
    }
}