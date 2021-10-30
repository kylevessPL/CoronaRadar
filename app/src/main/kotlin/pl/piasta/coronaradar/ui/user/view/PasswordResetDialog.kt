package pl.piasta.coronaradar.ui.user.view

import android.app.Dialog
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.Observable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.PasswordResetDialogBinding
import pl.piasta.coronaradar.ui.common.view.TouchBehaviorDialog
import pl.piasta.coronaradar.ui.user.viewmodel.PasswordResetViewModel
import pl.piasta.coronaradar.ui.user.viewmodel.UserViewModel
import splitties.alertdialog.appcompat.onShow
import splitties.alertdialog.appcompat.positiveButton
import splitties.resources.str
import splitties.views.material.string

@AndroidEntryPoint
class PasswordResetDialog(private val oob: String) : DialogFragment() {

    private var _viewBinding: PasswordResetDialogBinding? = null
    private val viewBinding get() = _viewBinding!!

    private var _onPropertyChangedCallback: Observable.OnPropertyChangedCallback? = null
    private val onPropertyChangedCallback get() = _onPropertyChangedCallback!!

    private val viewModel: PasswordResetViewModel by viewModels()
    private val activityViewModel: UserViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
        viewBinding.lifecycleOwner = this
        viewBinding.viewModel = viewModel
        registerOnPropertyChangedCallback()
    }

    override fun onStop() {
        super.onStop()
        unregisterOnPropertyChangedCallback()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _viewBinding = PasswordResetDialogBinding.inflate(layoutInflater)
        return TouchBehaviorDialog(requireContext(), theme).apply {
            setView(viewBinding.root)
            setTitle(R.string.password_reset)
            setMessage(str(R.string.password_reset_message))
            setButton(BUTTON_POSITIVE, str(R.string.update)) { _, _ ->
                activityViewModel.resetPassword(oob, viewBinding.passwordResetPasswordInput.string)
            }
            setButton(BUTTON_NEGATIVE, str(android.R.string.cancel)) { _, _ -> }
        }.onShow { positiveButton.isEnabled = false }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    private fun registerOnPropertyChangedCallback() {
        val positiveButton = (dialog as AlertDialog).positiveButton
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