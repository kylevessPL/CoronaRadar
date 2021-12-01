package pl.piasta.coronaradar.ui.stats.view

import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.databinding.SurveyDetailsDialogBinding
import pl.piasta.coronaradar.ui.base.BaseDialogFragment
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsViewModel
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.TAG

@AndroidEntryPoint
class SurveyDetailsDialogFragment :
    BaseDialogFragment<SurveyDetailsDialogBinding, StatsViewModel>(R.layout.survey_details_dialog) {

    override val dialogTheme = R.style.Theme_CoronaRadar_Dialog_FullScreen

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
        dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
        val data = requireArguments().getParcelable<Survey>("data")!!
        binding.survey = data
        super.setupView()
    }

    override fun updateUI() {
        parentViewModel.surveyDetailsDialogDismiss.observeNotNull(viewLifecycleOwner) { dismiss() }
        parentViewModel.displayIllnessDetails.observeNotNull(viewLifecycleOwner) {
            displaySurveyDetailsInfoDialog(
                it
            )
        }
    }

    private fun displaySurveyDetailsInfoDialog(illnesses: Boolean) {
        when (illnesses) {
            true -> SurveyDetailsIllnessesInfoDialogFragment.newInstance(binding.survey!!).show(
                childFragmentManager,
                SurveyDetailsIllnessesInfoDialogFragment.TAG
            )
            false -> SurveyDetailsSymptomsInfoDialogFragment.newInstance(binding.survey!!).show(
                childFragmentManager,
                SurveyDetailsSymptomsInfoDialogFragment.TAG
            )
        }
    }
}