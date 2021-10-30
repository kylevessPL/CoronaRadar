package pl.piasta.coronaradar.ui.user.view

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.user.viewmodel.UserViewModel
import splitties.alertdialog.appcompat.cancelButton
import splitties.alertdialog.appcompat.messageResource
import splitties.alertdialog.appcompat.positiveButton
import splitties.alertdialog.appcompat.titleResource
import splitties.alertdialog.material.materialAlertDialog

@AndroidEntryPoint
class VerificationEmailDialog : DialogFragment() {

    private val activityViewModel: UserViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireContext().materialAlertDialog {
            titleResource = R.string.login_failure
            messageResource = R.string.verification_email_message
            positiveButton(R.string.resend_verification_email) { activityViewModel.sendVerificationEmail() }
            cancelButton()
        }
    }
}