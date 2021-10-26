package pl.piasta.coronaradar.ui.user.view

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.user.viewmodel.SignOutViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.ResultState.Error
import pl.piasta.coronaradar.util.ifTrue
import splitties.alertdialog.appcompat.cancelButton
import splitties.alertdialog.appcompat.messageResource
import splitties.alertdialog.appcompat.positiveButton
import splitties.alertdialog.appcompat.titleResource
import splitties.alertdialog.material.materialAlertDialog
import splitties.toast.longToast

@AndroidEntryPoint
class SignOutDialog : DialogFragment() {

    private val viewModel: SignOutViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireContext().materialAlertDialog {
            titleResource = R.string.signout
            messageResource = R.string.signout_message
            positiveButton(R.string.signout) { viewModel.signOut() }
            cancelButton()
        }
    }

    private fun updateUI() {
        viewModel.signOutResult.observeNotNull(viewLifecycleOwner, { displaySignOutResult(it) })
    }

    private fun displaySignOutResult(result: ResultState<Nothing>) {
        (result is Error).ifTrue { longToast(R.string.general_failure_message) }
    }
}