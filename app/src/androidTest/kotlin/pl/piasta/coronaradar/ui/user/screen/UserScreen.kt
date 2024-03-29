package pl.piasta.coronaradar.ui.user.screen

import android.content.Intent.*
import androidx.test.espresso.intent.matcher.BundleMatchers.hasEntry
import androidx.test.espresso.intent.matcher.BundleMatchers.hasKey
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.dialog.KAlertDialog
import io.github.kakaocup.kakao.edit.KTextInputLayout
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.intent.KIntent
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matchers.allOf
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.user.view.UserActivity
import pl.piasta.coronaradar.util.IMAGE_MIME_TYPE
import splitties.resources.appStr

object UserScreen : KScreen<UserScreen>() {

    override val layoutId = R.layout.activity_user
    override val viewClass = UserActivity::class.java

    val signInAction = KTextView { withId(R.id.login_link) }
    val signUpAction = KTextView { withId(R.id.register_link) }
    val forgotPasswordAction = KTextView { withId(R.id.forgot_password_link) }
    val emailLoginInput = KTextInputLayout { withId(R.id.login_email_input) }
    val passwordLoginInput = KTextInputLayout { withId(R.id.login_password_input) }
    val emailRegisterInput = KTextInputLayout { withId(R.id.register_email_input) }
    val passwordRegisterInput = KTextInputLayout { withId(R.id.register_password_input) }
    val passwordConfirmRegisterInput =
        KTextInputLayout { withId(R.id.register_password_confirm_input) }
    val displayNameAccountInput = KTextInputLayout { withId(R.id.account_display_name_input) }
    val emailAccountInput = KTextInputLayout { withId(R.id.account_email_input) }
    val passwordAccountInput = KTextInputLayout { withId(R.id.account_password_input) }
    val passwordConfirmAccountInput =
        KTextInputLayout { withId(R.id.account_password_confirm_input) }
    val displayNameEditButton =
        KButton { withContentDescription(R.string.edit_display_name) }
    val passwordEditButton = KButton { withContentDescription(R.string.edit_password) }
    val togglePasswordButton = KButton { withContentDescription(R.string.toggle_password) }
    val togglePasswordConfirmButton =
        KButton { withContentDescription(R.string.toggle_password_confirm) }
    val updateAvatarButon = KButton { withId(R.id.upload_avatar) }
    val avatarImage = KImageView { withId(R.id.avatar) }
    val chooseImageIntent = KIntent {
        hasAction(ACTION_CHOOSER)
        hasExtras(
            allOf(
                hasEntry(
                    EXTRA_INTENT, allOf(
                        IntentMatchers.hasAction(ACTION_GET_CONTENT),
                        IntentMatchers.hasType(IMAGE_MIME_TYPE)
                    )
                ),
                hasKey(EXTRA_INITIAL_INTENTS),
                hasEntry(EXTRA_TITLE, appStr(R.string.set_avatar))
            )
        )
    }

    val signInButton = KButton { withId(R.id.signin_button) }
    val signUpButton = KButton { withId(R.id.signup_button) }
    val signOutButton = KButton { withId(R.id.signout_button) }
    val saveChangesButton = KButton { withId(R.id.save_changes_button) }
    val alertDialog = KAlertDialog()
}