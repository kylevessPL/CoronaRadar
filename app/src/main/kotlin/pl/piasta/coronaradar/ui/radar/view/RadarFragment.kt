package pl.piasta.coronaradar.ui.radar.view

import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.liveData
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.firebase.ml.modeldownloader.FirebaseMlException
import com.google.firebase.ml.modeldownloader.FirebaseMlException.NOT_ENOUGH_SPACE
import com.google.firebase.ml.modeldownloader.FirebaseMlException.NO_NETWORK_CONNECTION
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentRadarBinding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.common.model.OkDialogData
import pl.piasta.coronaradar.ui.common.view.OkDialogFragment
import pl.piasta.coronaradar.ui.radar.model.Classification
import pl.piasta.coronaradar.ui.radar.viewmodel.RadarViewModel
import pl.piasta.coronaradar.ui.util.newFragmentInstance
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.EMPTY
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.ResultState.*
import pl.piasta.coronaradar.util.TAG
import pl.piasta.coronaradar.util.ifTrue
import splitties.resources.str
import splitties.toast.longToast

@AndroidEntryPoint
class RadarFragment :
    BaseFragment<FragmentRadarBinding, RadarViewModel>(R.string.radar, R.layout.fragment_radar) {

    private val permissionsRequest by lazy { permissionsBuilder(RECORD_AUDIO).build() }

    private val appSettingsIntent by lazy {
        Intent().apply {
            action = ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
    }

    override val viewModel: RadarViewModel by viewModels()

    override fun updateUI() {
        permissionsRequest.liveData().observe(viewLifecycleOwner) {
            displayRequestPermissionsResult(it)
        }
        viewModel.requestPermissions.observeNotNull(viewLifecycleOwner) {
            permissionsRequest.send()
        }
        viewModel.classificationResultDialogDismiss.observeNotNull(viewLifecycleOwner) {
            displaySurveyDialog()
        }
        viewModel.updateModelResult.observeNotNull(viewLifecycleOwner) {
            displayUpdateModelResult(it)
        }
        viewModel.classificationResult.observeNotNull(viewLifecycleOwner) {
            displayClassificationResult(it)
        }
        viewModel.saveUserHistoryResult.observeNotNull(viewLifecycleOwner) {
            displaySaveUserHistoryResult(it)
        }
    }

    private fun displaySurveyDialog() = SurveyDialogFragment().show(
        childFragmentManager,
        SurveyDialogFragment::class.TAG
    )

    private fun displayUpdateModelResult(result: ResultState<Nothing>) = when (result) {
        Loading -> {
            viewModel.setModelUpdateVisibility(true)
            viewModel.setCurrentOperationMessage(str(R.string.model_update_message))
        }
        else -> {
            viewModel.setModelUpdateVisibility(false)
            viewModel.setCurrentOperationMessage(String.EMPTY)
            when (result) {
                is Error -> result.ex.takeIf { it is FirebaseMlException }?.let {
                    when ((result.ex as FirebaseMlException).code) {
                        NO_NETWORK_CONNECTION -> longToast(R.string.no_internet_connection)
                        NOT_ENOUGH_SPACE -> longToast(R.string.not_enough_space)
                        else -> longToast(R.string.general_failure_message)
                    }
                } ?: longToast(R.string.general_failure_message)
                else -> viewModel.updateModelUpdatePreferences()
            }
        }
    }

    private fun displayClassificationResult(result: ResultState<Classification>) = when (result) {
        Loading -> {
            viewModel.setProcessingVisibility(true)
            viewModel.setCurrentOperationMessage(str(R.string.classification_message))
        }
        else -> {
            viewModel.setProcessingVisibility(false)
            viewModel.setCurrentOperationMessage(String.EMPTY)
            when (result) {
                is Success -> {
                    viewModel.saveUserHistory(result.data!!)
                    newFragmentInstance<ClassificationResultDialogFragment>(
                        ClassificationResultDialogFragment.DATA to result.data
                    )
                        .show(
                            childFragmentManager,
                            ClassificationResultDialogFragment::class.TAG
                        )
                }
                else -> longToast(R.string.general_failure_message)
            }
        }
    }

    private fun displaySaveUserHistoryResult(result: ResultState<Nothing>) =
        (result is Error).ifTrue {
            longToast(R.string.user_data_save_failure_message)
        }

    private fun displayRequestPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> newFragmentInstance<OkDialogFragment>(
                OkDialogFragment.DATA to OkDialogData(
                    R.string.permissions_required,
                    R.string.permissions_required_message,
                    { startActivity(appSettingsIntent) },
                    R.string.permissions_open_settings,
                    true
                )
            ).show(
                parentFragmentManager,
                OkDialogFragment::class.TAG
            )
            result.anyShouldShowRationale() -> newFragmentInstance<OkDialogFragment>(
                OkDialogFragment.DATA to OkDialogData(
                    R.string.permissions_required,
                    R.string.permissions_required_message,
                    { permissionsRequest.send() },
                    R.string.retry,
                    true
                )
            ).show(
                parentFragmentManager,
                OkDialogFragment::class.TAG
            )
            result.allGranted() -> when (viewModel.isRecording.value) {
                true -> lifecycleScope.launch {
                    viewModel.analyzeData()
                }
                else -> lifecycleScope.launch {
                    viewModel.recordData()
                }
            }
        }
    }
}
