package pl.piasta.coronaradar.ui.radar.view

import android.content.Context
import android.view.View
import android.view.View.OnAttachStateChangeListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.quickbirdstudios.surveykit.*
import com.quickbirdstudios.surveykit.backend.views.main_parts.AbortDialogConfiguration
import com.quickbirdstudios.surveykit.backend.views.questions.IntroQuestionView
import com.quickbirdstudios.surveykit.result.StepResult
import com.quickbirdstudios.surveykit.steps.CompletionStep
import com.quickbirdstudios.surveykit.steps.CompletionStep.LottieAnimation
import com.quickbirdstudios.surveykit.steps.InstructionStep
import com.quickbirdstudios.surveykit.steps.QuestionStep
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.common.CommonIllness
import pl.piasta.coronaradar.data.common.CommonSymptom
import pl.piasta.coronaradar.data.common.Gender
import pl.piasta.coronaradar.databinding.SurveyDialogBinding
import pl.piasta.coronaradar.ui.base.BaseBottomSheetDialogFragment
import pl.piasta.coronaradar.ui.radar.viewmodel.RadarViewModel
import pl.piasta.coronaradar.ui.util.expandDialog
import pl.piasta.coronaradar.ui.util.observeNotNull
import pl.piasta.coronaradar.util.EMPTY
import splitties.resources.color
import splitties.resources.str

@AndroidEntryPoint
class SurveyDialogFragment :
    BaseBottomSheetDialogFragment<SurveyDialogBinding, RadarViewModel>(R.layout.survey_dialog) {

    override val parentViewModel: RadarViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun setupView() = setupSurvey()

    override fun updateUI() =
        parentViewModel.collectSurveyDataResult.observeNotNull(viewLifecycleOwner) { dismiss() }

    private fun setupSurvey() {
        val steps = listOf(
            object : InstructionStep(
                title = str(R.string.survey_intro_title),
                text = str(R.string.survey_intro_text),
                buttonText = str(R.string.start)
            ) {
                override fun createView(
                    context: Context,
                    stepResult: StepResult?
                ): IntroQuestionView {
                    return super.createView(context, stepResult).apply {
                        addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
                            override fun onViewAttachedToWindow(v: View?) {}
                            override fun onViewDetachedFromWindow(v: View?) = expandDialog()
                        })
                    }
                }
            },
            QuestionStep(
                id = StepIdentifier(id = 0.toString()),
                title = str(R.string.name_question_title),
                text = str(R.string.name_question_message),
                nextButtonText = str(R.string.next),
                answerFormat = AnswerFormat.TextAnswerFormat(
                    hintText = str(R.string.your_name),
                    maxLines = 1
                )
            ),
            QuestionStep(
                id = StepIdentifier(id = 1.toString()),
                title = str(R.string.age_question_title),
                text = str(R.string.age_question_message),
                nextButtonText = str(R.string.next),
                answerFormat = AnswerFormat.IntegerAnswerFormat(
                    hint = str(R.string.your_age)
                )
            ),
            QuestionStep(
                id = StepIdentifier(id = 2.toString()),
                title = str(R.string.gender_question_title),
                text = str(R.string.gender_question_message),
                nextButtonText = str(R.string.next),
                answerFormat = AnswerFormat.ValuePickerAnswerFormat(
                    choices = Gender.values().map { str(it.label) }
                )
            ),
            QuestionStep(
                id = StepIdentifier(id = 3.toString()),
                title = str(R.string.country_question_title),
                text = str(R.string.country_question_message),
                nextButtonText = str(R.string.next),
                answerFormat = AnswerFormat.TextAnswerFormat(
                    hintText = str(R.string.your_country),
                    maxLines = 1
                )
            ),
            QuestionStep(
                id = StepIdentifier(id = 4.toString()),
                title = str(R.string.illnesses_question_title),
                text = str(R.string.choose_all_applicable_message),
                nextButtonText = str(R.string.next),
                skipButtonText = str(R.string.skip),
                isOptional = true,
                answerFormat = AnswerFormat.MultipleChoiceAnswerFormat(
                    textChoices = CommonIllness.values().map { TextChoice(str(it.label)) }
                )
            ),
            QuestionStep(
                id = StepIdentifier(id = 5.toString()),
                title = str(R.string.quarantine_question_title),
                text = str(R.string.yes_no_message),
                nextButtonText = str(R.string.next),
                answerFormat = AnswerFormat.BooleanAnswerFormat(
                    positiveAnswerText = str(R.string.yes),
                    negativeAnswerText = str(R.string.no)
                )
            ),
            QuestionStep(
                id = StepIdentifier(id = 6.toString()),
                title = str(R.string.close_contact_question_title),
                text = str(R.string.yes_no_message),
                nextButtonText = str(R.string.next),
                answerFormat = AnswerFormat.BooleanAnswerFormat(
                    positiveAnswerText = str(R.string.yes),
                    negativeAnswerText = str(R.string.no)
                )
            ),
            QuestionStep(
                id = StepIdentifier(id = 7.toString()),
                title = str(R.string.travel_abroad_question_title),
                text = str(R.string.yes_no_message),
                nextButtonText = str(R.string.next),
                answerFormat = AnswerFormat.BooleanAnswerFormat(
                    positiveAnswerText = str(R.string.yes),
                    negativeAnswerText = str(R.string.no)
                )
            ),
            QuestionStep(
                id = StepIdentifier(id = 8.toString()),
                title = str(R.string.smoker_question_title),
                text = str(R.string.yes_no_message),
                nextButtonText = str(R.string.next),
                answerFormat = AnswerFormat.BooleanAnswerFormat(
                    positiveAnswerText = str(R.string.yes),
                    negativeAnswerText = str(R.string.no)
                )
            ),
            QuestionStep(
                id = StepIdentifier(id = 9.toString()),
                title = str(R.string.symptoms_question_title),
                text = str(R.string.choose_all_applicable_message),
                nextButtonText = str(R.string.next),
                skipButtonText = str(R.string.skip),
                isOptional = true,
                answerFormat = AnswerFormat.MultipleChoiceAnswerFormat(
                    textChoices = CommonSymptom.values().map { TextChoice(str(it.label)) }
                )
            ),
            QuestionStep(
                id = StepIdentifier(id = 10.toString()),
                title = str(R.string.wellbeing_question_title),
                text = str(R.string.wellbeing_question_message),
                nextButtonText = str(R.string.next),
                answerFormat = AnswerFormat.ScaleAnswerFormat(
                    minimumValue = 1,
                    maximumValue = 10,
                    minimumValueDescription = String.EMPTY,
                    maximumValueDescription = String.EMPTY,
                    step = 1F
                )
            ),
            CompletionStep(
                title = str(R.string.survey_complete_title),
                text = str(R.string.survey_complete_message),
                buttonText = str(R.string.submit),
                lottieAnimation = LottieAnimation.Asset(str(R.string.success_anim_path))
            )
        )
        val task = OrderedTask(steps)
        val configuration = SurveyTheme(
            themeColorDark = color(R.color.picton_blue_600),
            themeColor = color(R.color.picton_blue_600),
            textColor = color(R.color.black),
            abortDialogConfiguration = AbortDialogConfiguration(
                title = R.string.survey_cancel_title,
                message = R.string.survey_cancel_message,
                neutralMessage = R.string.no,
                negativeMessage = R.string.yes
            )
        )
        lifecycleScope.launchWhenStarted {
            binding.surveyView.start(task, configuration)
        }
    }
}