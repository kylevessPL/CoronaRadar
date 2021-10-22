package pl.piasta.coronaradar.ui.login

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState.Error
import pl.piasta.coronaradar.util.ResultState.Success
import splitties.alertdialog.appcompat.messageResource
import splitties.alertdialog.appcompat.negativeButton
import splitties.alertdialog.appcompat.positiveButton
import splitties.alertdialog.appcompat.titleResource
import splitties.alertdialog.material.materialAlertDialog
import splitties.toast.longToast

@AndroidEntryPoint
class VerificationDialog : DialogFragment() {

    private val viewModel: VerificationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireContext().materialAlertDialog {
            titleResource = R.string.login_failure
            messageResource = R.string.user_not_verified
            positiveButton(R.string.resend_verification_email) { viewModel.verify() }
            negativeButton(R.string.cancel) {}
        }
    }

    private fun updateUI() {
        viewModel.verificationResult.observeNotNull(viewLifecycleOwner, { result ->
            when (result) {
                is Success -> longToast(R.string.verification_email_success_message)
                is Error -> longToast(R.string.general_failure_message)
                else -> {
                }
            }
        })
    }
}