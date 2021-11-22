package pl.piasta.coronaradar.ui.user.view

import androidx.appcompat.app.AlertDialog
import androidx.databinding.Observable
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.PasswordResetDialogBinding
import pl.piasta.coronaradar.ui.base.BaseFormDialogFragment
import pl.piasta.coronaradar.ui.user.viewmodel.PasswordResetViewModel
import pl.piasta.coronaradar.ui.user.viewmodel.UserViewModel
import splitties.alertdialog.appcompat.positiveButton
import splitties.views.material.string

@AndroidEntryPoint
class PasswordResetDialogFragment(private val oob: String) :
    BaseFormDialogFragment<PasswordResetDialogBinding, PasswordResetViewModel>(
        R.layout.password_reset_dialog,
        R.string.password_reset,
        R.string.password_reset_message
    ) {

    private var _onPropertyChangedCallback: Observable.OnPropertyChangedCallback? = null
    private val onPropertyChangedCallback get() = _onPropertyChangedCallback!!

    override val viewModel: PasswordResetViewModel by viewModels()
    private val activityViewModel: UserViewModel by activityViewModels()

    override val positiveButtonRes: Int = R.string.update

    override fun onPositiveButtonClick() =
        activityViewModel.resetPassword(oob, viewBinding.passwordResetPasswordInput.string)

    override fun registerOnPropertyChangedCallback() {
        val positiveButton = (dialog as AlertDialog).positiveButton
        _onPropertyChangedCallback = object : Observable.OnPropertyChangedCallback() {

            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                positiveButton.isEnabled = viewModel.passwordResetForm.isPasswordResetFormValid()
            }
        }
        viewModel.passwordResetForm.addOnPropertyChangedCallback(onPropertyChangedCallback)
    }

    override fun unregisterOnPropertyChangedCallback() {
        viewModel.passwordResetForm.removeOnPropertyChangedCallback(onPropertyChangedCallback)
        _onPropertyChangedCallback = null
    }
}