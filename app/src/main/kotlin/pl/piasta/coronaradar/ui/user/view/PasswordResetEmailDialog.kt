package pl.piasta.coronaradar.ui.user.view

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.Observable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.PasswordResetEmailDialogBinding
import pl.piasta.coronaradar.ui.user.viewmodel.PasswordResetEmailViewModel
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
import splitties.views.material.string

@AndroidEntryPoint
class PasswordResetEmailDialog : DialogFragment() {

    private var _viewBinding: PasswordResetEmailDialogBinding? = null
    private val viewBinding get() = _viewBinding!!

    private var _onPropertyChangedCallback: Observable.OnPropertyChangedCallback? = null
    private val onPropertyChangedCallback get() = _onPropertyChangedCallback!!

    private val viewModel: PasswordResetEmailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.lifecycleOwner = this@PasswordResetEmailDialog
        viewBinding.viewModel = viewModel
        updateUI()
    }

    override fun onStart() {
        super.onStart()
        registerOnPropertyChangedCallback()
    }

    override fun onStop() {
        super.onStop()
        unregisterOnPropertyChangedCallback()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _viewBinding = PasswordResetEmailDialogBinding.inflate(layoutInflater)
        return requireContext().materialAlertDialog {
            setView(viewBinding.root)
            titleResource = R.string.password_reset
            messageResource = R.string.password_reset_email_message
            positiveButton(R.string.send) { viewModel.sendPasswordResetEmail(viewBinding.passwordResetEmailInput.string) }
            cancelButton()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    private fun updateUI() {
        viewModel.passwordResetEmailResult.observeNotNull(
            viewLifecycleOwner,
            { displayPasswordResetEmailResult(it) })
    }

    private fun displayPasswordResetEmailResult(result: ResultState<Boolean>) = when (result) {
        is Success -> longToast(R.string.password_reset_email_complete_message)
        is Error -> longToast(R.string.general_failure_message)
        else -> {
        }
    }

    private fun registerOnPropertyChangedCallback() {
        val positiveButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        _onPropertyChangedCallback = object : Observable.OnPropertyChangedCallback() {

            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                positiveButton.isEnabled =
                    viewModel.passwordResetEmailForm.isPasswordResetEmailFormValid()
            }
        }
        viewModel.passwordResetEmailForm.addOnPropertyChangedCallback(onPropertyChangedCallback)
    }

    private fun unregisterOnPropertyChangedCallback() {
        viewModel.passwordResetEmailForm.removeOnPropertyChangedCallback(onPropertyChangedCallback)
        _onPropertyChangedCallback = null
    }
}