package pl.piasta.coronaradar.example.screen

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KSnackbar
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.example.view.ExampleActivity

object ExampleScreen : KScreen<ExampleScreen>() {

    override val layoutId: Int = R.layout.activity_example
    override val viewClass: Class<*> = ExampleActivity::class.java

    val fab = KButton {
        withId(R.id.fab)
    }

    val snackbar = KSnackbar()
}