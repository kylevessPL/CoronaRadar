package pl.piasta.coronaradar.ui.radar.view

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.ClassificationResultDialogBinding
import pl.piasta.coronaradar.ui.base.BaseDialogFragment
import pl.piasta.coronaradar.ui.radar.model.Classification
import pl.piasta.coronaradar.ui.radar.viewmodel.RadarViewModel
import splitties.views.onClick

@AndroidEntryPoint
class ClassificationResultDialogFragment :
    BaseDialogFragment<ClassificationResultDialogBinding, RadarViewModel>(R.layout.classification_result_dialog) {

    override val dialogTheme = R.style.Theme_CoronaRadar_Dialog_NoBackground

    override val viewModel: RadarViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun doOnDismiss() {
        viewModel.classificationResultDialogDismissEvent()
    }

    override fun setupView() {
        val data = requireArguments().getParcelable<Classification>("data")!!
        with(binding) {
            classification = data
            classificationButton.onClick { dismiss() }
        }
    }
}