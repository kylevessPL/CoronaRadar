package pl.piasta.coronaradar.ui.settings.screen

import android.view.View
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.dialog.KAlertDialog
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KTextView
import io.github.kakaocup.kakao.toolbar.KToolbar
import org.hamcrest.Matcher
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.settings.view.SettingsActivity

object SettingsScreen : KScreen<SettingsScreen>() {

    override val layoutId = R.layout.activity_settings
    override val viewClass = SettingsActivity::class.java

    val toolbar = KToolbar { withId(androidx.appcompat.R.id.action_bar) }
    val preferenceList = KRecyclerView({
        withId(androidx.preference.R.id.recycler_view)
    }, itemTypeBuilder = {
        itemType(::PreferenceItem)
    })
    val alertDialog = KAlertDialog()
}

class PreferenceItem(parent: Matcher<View>) : KRecyclerItem<PreferenceItem>(parent) {
    val languagePreference = KTextView { withText(R.string.language) }
    val coronaKitUpdateFrequencyPreference =
        KTextView { withText(R.string.coronakit_update_frequency) }
    val coronaKitUpdateWifiOnlyPreference =
        KTextView { withText(R.string.coronakit_update_wifi_only) }
}