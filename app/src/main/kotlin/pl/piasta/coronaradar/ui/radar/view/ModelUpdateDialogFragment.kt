@file:Suppress("DEPRECATION")

package pl.piasta.coronaradar.ui.radar.view

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.radar.viewmodel.RadarViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.ResultState.Loading
import pl.piasta.coronaradar.util.ifTrue
import splitties.resources.str

@AndroidEntryPoint
class ModelUpdateDialogFragment : DialogFragment() {

    private val fragmentViewModel: RadarViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onStart() {
        super.onStart()
        fragmentViewModel.updateModelResult.observeNotNull(requireParentFragment(), { result ->
            (result != Loading).ifTrue {
                dismiss()
            }
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        ProgressDialog(requireContext(), theme).apply {
            setTitle(R.string.please_wait)
            setMessage(str(R.string.model_update_message))
            isCancelable = false
        }
}