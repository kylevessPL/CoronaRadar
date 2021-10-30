package pl.piasta.coronaradar.ui.common.view

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.ui.common.model.OkDialogData
import splitties.alertdialog.appcompat.message
import splitties.alertdialog.appcompat.okButton
import splitties.alertdialog.appcompat.title
import splitties.alertdialog.material.materialAlertDialog

@AndroidEntryPoint
class OkDialogFragment : DialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(data: OkDialogData): DialogFragment {
            val okDialog = OkDialogFragment()
            val args = Bundle()
            args.putParcelable("data", data)
            okDialog.arguments = args
            return okDialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val data = requireArguments().getParcelable<OkDialogData>("data")!!
        return requireContext().materialAlertDialog {
            title = data.title
            message = data.message
            okButton { data.positiveAction() }
        }
    }
}