package pl.piasta.coronaradar.ui.stats.view

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.databinding.SurveyDetailsDialogBinding
import pl.piasta.coronaradar.ui.base.BaseFullScreenDialogFragment
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.TAG

@AndroidEntryPoint
class SurveyDetailsDialogFragment :
    BaseFullScreenDialogFragment<SurveyDetailsDialogBinding, StatsViewModel>(
        R.layout.survey_details_dialog,
        R.string.survey_result
    ) {

    override lateinit var actionBar: Toolbar

    override val parentViewModel: StatsViewModel by viewModels(ownerProducer = { requireParentFragment() })

    companion object {
        @JvmStatic
        fun newInstance(data: Survey): SurveyDetailsDialogFragment {
            val dialog = SurveyDetailsDialogFragment()
            val args = Bundle()
            args.putParcelable("data", data)
            return dialog.apply {
                arguments = args
            }
        }
    }

    override fun setupView() {
        val data = requireArguments().getParcelable<Survey>("data")!!
        with(binding) {
            actionBar = toolbar
            survey = data
        }
        super.setupView()
    }

    override fun updateUI() {
        parentViewModel.displayIllnessDetails.observeNotNull(viewLifecycleOwner) { illnesses ->
            SurveyDetailsInfoDialogFragment.newInstance(illnesses, binding.survey!!).show(
                childFragmentManager,
                SurveyDetailsInfoDialogFragment.TAG
            )
        }
    }
}