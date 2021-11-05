package pl.piasta.coronaradar.ui.user.view

import androidx.appcompat.app.AlertDialog
import androidx.databinding.Observable
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.PasswordResetEmailDialogBinding
import pl.piasta.coronaradar.ui.base.BaseFormDialogFragment
import pl.piasta.coronaradar.ui.user.viewmodel.PasswordResetEmailViewModel
import pl.piasta.coronaradar.ui.user.viewmodel.UserViewModel
import splitties.alertdialog.appcompat.positiveButton
import splitties.views.material.string

@AndroidEntryPoint
class PasswordResetEmailDialog :
    BaseFormDialogFragment<PasswordResetEmailDialogBinding, PasswordResetEmailViewModel, UserViewModel>(
        R.layout.password_reset_email_dialog,
        R.string.password_reset,
        R.string.password_reset_email_message
    ) {

    private var _onPropertyChangedCallback: Observable.OnPropertyChangedCallback? = null
    private val onPropertyChangedCallback get() = _onPropertyChangedCallback!!

    override val viewModel: PasswordResetEmailViewModel by viewModels()
    override val activityViewModel: UserViewModel by activityViewModels()

    override val positiveButtonRes: Int = R.string.send

    override fun onPositiveButtonClick() =
        activityViewModel.sendPasswordResetEmail(viewBinding.passwordResetEmailInput.string)

    override fun registerOnPropertyChangedCallback() {
        val positiveButton = (dialog as AlertDialog).positiveButton
        _onPropertyChangedCallback = object : Observable.OnPropertyChangedCallback() {

            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                positiveButton.isEnabled =
                    viewModel.passwordResetEmailForm.isPasswordResetEmailFormValid()
            }
        }
        viewModel.passwordResetEmailForm.addOnPropertyChangedCallback(onPropertyChangedCallback)
    }

    override fun unregisterOnPropertyChangedCallback() {
        viewModel.passwordResetEmailForm.removeOnPropertyChangedCallback(onPropertyChangedCallback)
        _onPropertyChangedCallback = null
    }
}