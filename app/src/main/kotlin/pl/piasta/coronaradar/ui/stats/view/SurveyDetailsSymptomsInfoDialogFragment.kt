package pl.piasta.coronaradar.ui.stats.view

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.ui.base.BaseBottomSheetDialogFragment
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsViewModel

@AndroidEntryPoint
class SurveyDetailsSymptomsInfoDialogFragment :
    BaseBottomSheetDialogFragment<ViewDataBinding, StatsViewModel>(R.layout.survey_details_symptoms_info_dialog) {

    override val dialogTheme = R.style.Theme_CoronaRadar_Dialog_NoBackground

    companion object {
        @JvmStatic
        fun newInstance(data: Survey): SurveyDetailsSymptomsInfoDialogFragment {
            val dialog = SurveyDetailsSymptomsInfoDialogFragment()
            val args = Bundle()
            args.putParcelable("data", data)
            return dialog.apply {
                arguments = args
            }
        }
    }

    override fun setupView() {
        val data = requireArguments().getParcelable<Survey>("data")!!
        binding.setVariable(BR.survey, data)
        super.setupView()
    }
}