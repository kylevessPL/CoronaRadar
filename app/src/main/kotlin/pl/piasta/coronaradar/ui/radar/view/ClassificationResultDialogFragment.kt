package pl.piasta.coronaradar.ui.radar.view

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.common.ResultLabel.NEGATIVE
import pl.piasta.coronaradar.data.common.ResultLabel.POSITIVE
import pl.piasta.coronaradar.databinding.ClassificationResultDialogBinding
import pl.piasta.coronaradar.ui.base.BaseCustomViewDialogFragment
import pl.piasta.coronaradar.ui.radar.model.Classification
import pl.piasta.coronaradar.ui.radar.viewmodel.RadarViewModel
import splitties.resources.str
import splitties.views.imageResource
import splitties.views.onClick
import splitties.views.textColorResource
import splitties.views.textResource

@AndroidEntryPoint
class ClassificationResultDialogFragment :
    BaseCustomViewDialogFragment<ClassificationResultDialogBinding, RadarViewModel>(R.layout.classification_result_dialog) {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_CoronaRadar_AlertDialog_NoBackground)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        parentViewModel.classificationResultDialogDismissEvent()
    }

    override fun setupView() {
        val data = requireArguments().getParcelable<Classification>("data")!!
        with(binding) {
            classificationProbability.text =
                data.probability.toString().plus(str(R.string.percent))
            when (data.result) {
                POSITIVE -> {
                    classificationResult.textResource = R.string.positive
                    classificationResult.textColorResource = R.color.red_failure
                    classificationResultIcon.imageResource = R.drawable.ic_sad
                }
                NEGATIVE -> {
                    classificationResult.textResource = R.string.negative
                    classificationResult.textColorResource = R.color.green_success
                    classificationResultIcon.imageResource = R.drawable.ic_smiley
                }
            }
            classificationButton.onClick { dismiss() }
        }
    }
}