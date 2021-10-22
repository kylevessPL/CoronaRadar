package pl.piasta.coronaradar.ui.login

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.PasswordResetDialogBinding
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState.Error
import pl.piasta.coronaradar.util.ResultState.Success
import splitties.alertdialog.appcompat.cancelButton
import splitties.alertdialog.appcompat.messageResource
import splitties.alertdialog.appcompat.positiveButton
import splitties.alertdialog.appcompat.titleResource
import splitties.alertdialog.material.materialAlertDialog
import splitties.toast.longToast
import splitties.views.material.string

@AndroidEntryPoint
class PasswordResetDialog : DialogFragment() {

    private var _dialogBinding: PasswordResetDialogBinding? = null
    private val dialogBinding get() = _dialogBinding!!

    private val viewModel: PasswordResetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _dialogBinding = PasswordResetDialogBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireContext().materialAlertDialog {
            setView(dialogBinding.root)
            titleResource = R.string.password_reset
            messageResource = R.string.password_reset_message
            positiveButton(R.string.send) { viewModel.resetPassword(dialogBinding.passwordResetEmailInput.string) }
            cancelButton()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _dialogBinding = null
    }

    private fun updateUI() {
        viewModel.passwordResetResult.observeNotNull(viewLifecycleOwner, { result ->
            when (result) {
                is Success -> longToast(R.string.password_reset_complete_message)
                is Error -> longToast(R.string.general_failure_message)
                else -> {
                }
            }
        })
    }
}