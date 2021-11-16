package pl.piasta.coronaradar.ui.radar.view

import android.graphics.Color.TRANSPARENT
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.ClassificationResultDialogBinding
import pl.piasta.coronaradar.ui.radar.model.Classification
import pl.piasta.coronaradar.ui.radar.model.ClassificationResult.NEGATIVE
import pl.piasta.coronaradar.ui.radar.model.ClassificationResult.POSITIVE
import splitties.resources.str
import splitties.views.imageResource
import splitties.views.onClick
import splitties.views.textColorResource
import splitties.views.textResource

@AndroidEntryPoint
class ClassificationResultDialogFragment : DialogFragment() {

    private var _binding: ClassificationResultDialogBinding? = null
    private val binding get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance(data: Classification): ClassificationResultDialogFragment {
            val dialog = ClassificationResultDialogFragment()
            val args = Bundle()
            args.putParcelable("data", data)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ClassificationResultDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = requireArguments().getParcelable<Classification>("data")!!
        dialog?.window?.setBackgroundDrawable(ColorDrawable(TRANSPARENT))
        updateUI(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(data: Classification) = with(binding) {
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