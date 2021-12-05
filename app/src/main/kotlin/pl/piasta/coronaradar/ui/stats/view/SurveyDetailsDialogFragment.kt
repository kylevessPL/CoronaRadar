package pl.piasta.coronaradar.ui.stats.view

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.databinding.SurveyDetailsDialogBinding
import pl.piasta.coronaradar.ui.base.BaseDialogFragment
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsViewModel
import pl.piasta.coronaradar.ui.util.newFragmentInstance
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.TAG

@AndroidEntryPoint
class SurveyDetailsDialogFragment :
    BaseDialogFragment<SurveyDetailsDialogBinding, StatsViewModel>(R.layout.survey_details_dialog) {

    companion object {
        const val DATA = "data"
    }

    override val dialogTheme = R.style.Theme_CoronaRadar_Dialog_FullScreen

    override val viewModel: StatsViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun setupView() {
        dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
        val data = requireArguments().getParcelable<Survey>(DATA)!!
        binding.survey = data
    }

    override fun updateUI() {
        viewModel.surveyDetailsDialogDismiss.observeNotNull(viewLifecycleOwner) { dismiss() }
        viewModel.displayIllnessDetails.observeNotNull(viewLifecycleOwner) {
            displaySurveyDetailsInfoDialog(it)
        }
    }

    private fun displaySurveyDetailsInfoDialog(illnesses: Boolean) {
        when (illnesses) {
            true -> newFragmentInstance<SurveyDetailsIllnessesInfoDialogFragment>(
                SurveyDetailsIllnessesInfoDialogFragment.DATA to binding.survey!!
            ).show(
                childFragmentManager,
                SurveyDetailsIllnessesInfoDialogFragment.TAG
            )
            false -> newFragmentInstance<SurveyDetailsSymptomsInfoDialogFragment>(
                SurveyDetailsSymptomsInfoDialogFragment.DATA to binding.survey!!
            ).show(
                childFragmentManager,
                SurveyDetailsSymptomsInfoDialogFragment.TAG
            )
        }
    }
}