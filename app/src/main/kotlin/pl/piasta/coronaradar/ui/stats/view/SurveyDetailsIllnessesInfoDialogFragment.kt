package pl.piasta.coronaradar.ui.stats.view

import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.ui.base.BaseBottomSheetDialogFragment
import pl.piasta.coronaradar.ui.stats.viewmodel.StatsViewModel

@AndroidEntryPoint
class SurveyDetailsIllnessesInfoDialogFragment :
    BaseBottomSheetDialogFragment<ViewDataBinding, StatsViewModel>(R.layout.survey_details_illnesses_info_dialog) {

    companion object {
        const val DATA = "data"
    }

    override val dialogTheme = R.style.Theme_CoronaRadar_Dialog_NoBackground

    override fun setupView() {
        val data = requireArguments().getParcelable<Survey>(DATA)!!
        binding.setVariable(BR.survey, data)
    }
}