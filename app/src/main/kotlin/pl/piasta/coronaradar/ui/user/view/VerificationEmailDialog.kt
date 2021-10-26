package pl.piasta.coronaradar.ui.user.view

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.user.viewmodel.VerificationEmailViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.ResultState.Error
import pl.piasta.coronaradar.util.ResultState.Success
import splitties.alertdialog.appcompat.cancelButton
import splitties.alertdialog.appcompat.messageResource
import splitties.alertdialog.appcompat.positiveButton
import splitties.alertdialog.appcompat.titleResource
import splitties.alertdialog.material.materialAlertDialog
import splitties.toast.longToast

@AndroidEntryPoint
class VerificationEmailDialog : DialogFragment() {

    private val viewModel: VerificationEmailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireContext().materialAlertDialog {
            titleResource = R.string.login_failure
            messageResource = R.string.verification_email_message
            positiveButton(R.string.resend_verification_email) { viewModel.sendVerificationEmail() }
            cancelButton()
        }
    }

    private fun updateUI() {
        viewModel.verificationEmailResult.observeNotNull(
            viewLifecycleOwner,
            { displayVerificationEmailResult(it) })
    }

    private fun displayVerificationEmailResult(result: ResultState<Nothing>) = when (result) {
        is Success -> longToast(R.string.verification_email_success_message)
        is Error -> longToast(R.string.general_failure_message)
        else -> {
        }
    }
}