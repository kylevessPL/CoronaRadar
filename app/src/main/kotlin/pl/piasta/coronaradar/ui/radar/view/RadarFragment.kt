package pl.piasta.coronaradar.ui.radar.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentRadarBinding
import pl.piasta.coronaradar.ui.base.BaseFragment
import pl.piasta.coronaradar.ui.common.model.OkDialogData
import pl.piasta.coronaradar.ui.common.view.OkDialogFragment
import pl.piasta.coronaradar.ui.radar.viewmodel.RadarViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.TAG

@AndroidEntryPoint
class RadarFragment : BaseFragment<FragmentRadarBinding, RadarViewModel>(R.layout.fragment_radar) {

    private val permissionsRequest by lazy { permissionsBuilder(Manifest.permission.RECORD_AUDIO).build() }

    override val viewModel: RadarViewModel by viewModels()

    override fun updateUI() {
        viewModel.requestPermissions.observeNotNull(
            viewLifecycleOwner,
            { permissionsRequest.send() })
        permissionsRequest.liveData()
            .observe(viewLifecycleOwner, { displayRequestPermissionsResult(it) })
    }

    private fun displayRequestPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> OkDialogFragment.newInstance(
                OkDialogData(
                    R.string.permissions_required,
                    R.string.permissions_required_message,
                    { startActivity(createAppSettingsIntent()) },
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
                    false -> viewModel.recordStart()
                }
            }
        }
    }

    private fun createAppSettingsIntent() = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", requireContext().packageName, null)
    }
}
