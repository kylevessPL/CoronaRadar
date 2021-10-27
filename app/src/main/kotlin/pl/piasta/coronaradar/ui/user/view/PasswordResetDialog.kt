package pl.piasta.coronaradar.ui.user.view

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.Observable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.PasswordResetDialogBinding
import pl.piasta.coronaradar.ui.user.viewmodel.PasswordResetViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.ResultState.Error
import pl.piasta.coronaradar.util.ResultState.Success
import splitties.alertdialog.appcompat.cancelButton
import splitties.alertdialog.appcompat.messageResource
import splitties.alertdialog.appcompat.onShow
import splitties.alertdialog.appcompat.positiveButton
import splitties.alertdialog.appcompat.titleResource
import splitties.alertdialog.material.materialAlertDialog
import splitties.toast.longToast
import splitties.views.material.string

@AndroidEntryPoint
class PasswordResetDialog(private val oob: String) : DialogFragment() {

    private var _viewBinding: PasswordResetDialogBinding? = null
    private val viewBinding get() = _viewBinding!!

    private var _onPropertyChangedCallback: Observable.OnPropertyChangedCallback? = null
    private val onPropertyChangedCallback get() = _onPropertyChangedCallback!!

    private val viewModel: PasswordResetViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        viewBinding.lifecycleOwner = this
        viewBinding.viewModel = viewModel
        registerOnPropertyChangedCallback()
        updateUI()
    }

    override fun onStop() {
        super.onStop()
        unregisterOnPropertyChangedCallback()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _viewBinding = PasswordResetDialogBinding.inflate(layoutInflater)
        return requireContext().materialAlertDialog {
            setView(viewBinding.root)
            titleResource = R.string.password_reset
            messageResource = R.string.password_reset_message
            positiveButton(R.string.update) {
                viewModel.resetPassword(
                    oob,
                    viewBinding.passwordResetPasswordInput.string
                )
            }
            cancelButton()
        }.onShow { positiveButton.isEnabled = false }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    private fun updateUI() {
        viewModel.passwordResetResult.observeNotNull(
            this,
            { displayPasswordResetResult(it) })
    }

    private fun displayPasswordResetResult(result: ResultState<Boolean>) = when (result) {
        is Success -> longToast(R.string.password_reset_complete_message)
        is Error -> longToast(R.string.general_failure_message)
        else -> {
        }
    }

    private fun registerOnPropertyChangedCallback() {
        val positiveButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        _onPropertyChangedCallback = object : Observable.OnPropertyChangedCallback() {

            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                positiveButton.isEnabled = viewModel.passwordResetForm.isPasswordResetFormValid()
            }
        }
        viewModel.passwordResetForm.addOnPropertyChangedCallback(onPropertyChangedCallback)
    }

    private fun unregisterOnPropertyChangedCallback() {
        viewModel.passwordResetForm.removeOnPropertyChangedCallback(onPropertyChangedCallback)
        _onPropertyChangedCallback = null
    }
}