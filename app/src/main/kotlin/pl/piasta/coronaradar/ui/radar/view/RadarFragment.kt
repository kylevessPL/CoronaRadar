package pl.piasta.coronaradar.ui.radar.view

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.liveData
import com.fondesa.kpermissions.extension.permissionsBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentRadarBinding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.common.model.OkDialogData
import pl.piasta.coronaradar.ui.common.view.OkDialogFragment
import pl.piasta.coronaradar.ui.radar.viewmodel.RadarViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.ResultState.Error
import pl.piasta.coronaradar.util.ResultState.Loading
import pl.piasta.coronaradar.util.ResultState.Success
import pl.piasta.coronaradar.util.TAG
import pl.piasta.coronaradar.util.ifTrue
import splitties.toast.longToast
import splitties.toast.toast

@AndroidEntryPoint
class RadarFragment : BaseFragment<FragmentRadarBinding, RadarViewModel>(R.layout.fragment_radar) {

    private val permissionsRequest by lazy {
        permissionsBuilder(RECORD_AUDIO, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE).build()
    }

    private val appSettingsIntent by lazy {
        Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
    }

    override val viewModel: RadarViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            withContext(Dispatchers.IO) {
                viewModel.updateModel()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.recordStop()
    }

    override fun updateUI() {
        permissionsRequest.liveData()
            .observe(viewLifecycleOwner, { displayRequestPermissionsResult(it) })
        viewModel.requestPermissions.observeNotNull(
            viewLifecycleOwner,
            { permissionsRequest.send() })
        viewModel.updateModelResult.observeNotNull(
            viewLifecycleOwner,
            { displayUpdateModelResult(it) })
        viewModel.isRecording.observeNotNull(
            viewLifecycleOwner,
            { (!it).ifTrue { toast("stopped") } })
    }

    private fun displayUpdateModelResult(result: ResultState<Nothing>) = when (result) {
        is Success -> viewModel.updateModelUpdatePreferences()
        is Error -> longToast(R.string.general_failure_message)
        Loading -> ModelUpdateDialogFragment().show(
            parentFragmentManager,
            ModelUpdateDialogFragment::class.TAG
        )
    }

    private fun displayRequestPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> OkDialogFragment.newInstance(
                OkDialogData(
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
            result.anyShouldShowRationale() -> OkDialogFragment.newInstance(
                OkDialogData(
                    R.string.permissions_required,
                    R.string.permissions_required_message,
                    { permissionsRequest.send() },
                    R.string.permissions_request_again,
                    true
                )
            ).show(
                parentFragmentManager,
                OkDialogFragment::class.TAG
            )
            result.allGranted() -> lifecycleScope.launch(Dispatchers.IO) {
                when (viewModel.isRecording.value) {
                    true -> viewModel.recordStop()
                    else -> viewModel.recordStart()
                }
            }
        }
    }
}
