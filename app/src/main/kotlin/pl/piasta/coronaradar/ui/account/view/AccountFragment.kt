package pl.piasta.coronaradar.ui.account.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentAccountBinding
import pl.piasta.coronaradar.ui.account.viewmodel.AccountViewModel
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.common.model.OkDialogData
import pl.piasta.coronaradar.ui.common.view.OkDialogFragment
import pl.piasta.coronaradar.ui.user.viewmodel.UserViewModel
import pl.piasta.coronaradar.ui.util.fileSize
import pl.piasta.coronaradar.ui.util.newFragmentInstance
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.ui.util.observeNull
import pl.piasta.coronaradar.util.IMAGE_MIME_TYPE
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.ResultState.*
import pl.piasta.coronaradar.util.TAG
import pl.piasta.coronaradar.util.ifTrue
import splitties.resources.str
import splitties.toast.longToast

@AndroidEntryPoint
class AccountFragment(activityViewModelFactory: (() -> ViewModelProvider.Factory)? = null) :
    BaseFragment<FragmentAccountBinding, AccountViewModel>(
        R.string.my_account,
        R.layout.fragment_account
    ) {

    private val chooseAvatar =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            (result.resultCode == RESULT_OK).ifTrue {
                result.data?.data?.let { uri ->
                    uri.fileSize(requireContext()).takeIf { it / (1024 * 1024).toFloat() <= 2F }
                        ?.also {
                            viewModel.setUserAvatar(uri)
                        } ?: displayAvatarSizeTooLargeDialog()
                }
            }
        }

    override val viewModel: AccountViewModel by viewModels()
    private val activityViewModel: UserViewModel by activityViewModels(activityViewModelFactory)

    override fun updateUI() {
        activityViewModel.firebaseUser.observeNull(viewLifecycleOwner) {
            navigateToLoginFragment()
        }
        viewModel.signOut.observeNotNull(viewLifecycleOwner) {
            displaySignOutDialog()
        }
        viewModel.chooseAvatar.observeNotNull(viewLifecycleOwner) {
            launchChooseAvatarIntent()
        }
        viewModel.uploadUserAvatarResult.observeNotNull(viewLifecycleOwner) {
            displayUploadUserAvatarResult(it)
        }
        viewModel.updateUserProfileResult.observeNotNull(viewLifecycleOwner) {
            displayUpdateUserProfileResult(it)
        }
        activityViewModel.signOutResult.observeNotNull(viewLifecycleOwner) {
            displaySignOutResult(it)
        }
    }

    private fun navigateToLoginFragment() {
        val action = AccountFragmentDirections.actionAccountFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun launchChooseAvatarIntent() {
        val documentsIntent = Intent(ACTION_GET_CONTENT).apply {
            type = IMAGE_MIME_TYPE
        }
        val galleriesIntent = Intent(ACTION_PICK, EXTERNAL_CONTENT_URI).apply {
            type = IMAGE_MIME_TYPE
        }
        val chooserIntent = createChooser(
            documentsIntent,
            str(R.string.set_avatar)
        ).putExtra(EXTRA_INITIAL_INTENTS, arrayOf(galleriesIntent))
        chooseAvatar.launch(chooserIntent)
    }

    private fun displayAvatarSizeTooLargeDialog() {
        newFragmentInstance<OkDialogFragment>(
            OkDialogFragment.DATA to OkDialogData(
                R.string.set_avatar,
                R.string.set_avatar_size_failure
            )
        ).show(
            parentFragmentManager,
            OkDialogFragment::class.TAG
        )
    }

    private fun displaySignOutDialog() {
        newFragmentInstance<OkDialogFragment>(
            OkDialogFragment.DATA to OkDialogData(
                R.string.signout,
                R.string.signout_message,
                { activityViewModel.signOut() },
                R.string.signout,
                true
            )
        ).show(
            parentFragmentManager,
            OkDialogFragment::class.TAG
        )
    }

    private fun displayUploadUserAvatarResult(result: ResultState<Uri>) {
        when (result) {
            Loading -> viewModel.setProgressIndicatorVisibility(true)
            else -> {
                viewModel.setProgressIndicatorVisibility(false)
                (result is Error).ifTrue { longToast(R.string.general_failure_message) }
            }
        }
    }

    private fun displaySignOutResult(result: ResultState<Nothing>) = when (result) {
        is Success -> viewModel.setProgressIndicatorVisibility(false)
        is Error -> {
            viewModel.setProgressIndicatorVisibility(false)
            longToast(R.string.general_failure_message)
        }
        Loading -> viewModel.setProgressIndicatorVisibility(true)
    }

    private fun displayUpdateUserProfileResult(result: ResultState<Nothing>) {
        when (result) {
            is Success -> {
                viewModel.setProgressIndicatorVisibility(false)
                newFragmentInstance<OkDialogFragment>(
                    OkDialogFragment.DATA to OkDialogData(
                        R.string.update_profile_success,
                        R.string.update_profile_success_message
                    )
                ).show(
                    parentFragmentManager,
                    OkDialogFragment::class.TAG
                )
            }
            is Error -> {
                viewModel.setProgressIndicatorVisibility(false)
                when (result.ex) {
                    is FirebaseAuthRecentLoginRequiredException -> newFragmentInstance<OkDialogFragment>(
                        OkDialogFragment.DATA to OkDialogData(
                            R.string.signout,
                            R.string.reauthentication_required,
                            { activityViewModel.signOut() },
                            R.string.signout
                        )
                    ).show(
                        parentFragmentManager,
                        OkDialogFragment::class.TAG
                    )
                    else -> longToast(R.string.general_failure_message)
                }
            }
            Loading -> viewModel.setProgressIndicatorVisibility(true)
        }
    }
}