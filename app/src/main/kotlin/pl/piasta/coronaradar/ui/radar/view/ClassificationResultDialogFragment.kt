package pl.piasta.coronaradar.ui.radar.view

import android.os.Bundle
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.ClassificationResultDialogBinding
import pl.piasta.coronaradar.ui.base.BaseCustomViewDialogFragment
import pl.piasta.coronaradar.ui.radar.model.Classification
import pl.piasta.coronaradar.ui.radar.viewmodel.RadarViewModel
import splitties.views.onClick

@AndroidEntryPoint
class ClassificationResultDialogFragment :
    BaseCustomViewDialogFragment<ClassificationResultDialogBinding, RadarViewModel>(R.layout.classification_result_dialog) {

    override val dialogTheme = R.style.Theme_CoronaRadar_AlertDialog_NoBackground

    override val parentViewModel: RadarViewModel by viewModels(ownerProducer = { requireParentFragment() })

    companion object {
        @JvmStatic
        fun newInstance(data: Classification): ClassificationResultDialogFragment {
            val dialog = ClassificationResultDialogFragment()
            val args = Bundle()
            args.putParcelable("data", data)
            return dialog.apply {
                arguments = args
            }
        }
    }

    override fun doOnDismiss() {
        parentViewModel.classificationResultDialogDismissEvent()
    }

    override fun setupView() {
        val data = requireArguments().getParcelable<Classification>("data")!!
        with(binding) {
            classification = data
            classificationButton.onClick { dismiss() }
        }
    }
}