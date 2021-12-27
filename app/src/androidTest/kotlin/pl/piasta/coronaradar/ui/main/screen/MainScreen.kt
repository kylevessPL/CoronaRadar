package pl.piasta.coronaradar.ui.main.screen

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.common.views.KView
import io.github.kakaocup.kakao.drawer.KDrawerView
import io.github.kakaocup.kakao.navigation.KNavigationView
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import io.github.kakaocup.kakao.toolbar.KToolbar
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.ui.main.view.MainActivity

object MainScreen : KScreen<MainScreen>() {

    override val layoutId = R.layout.activity_main
    override val viewClass = MainActivity::class.java

    val toolbar = KToolbar { withId(R.id.toolbar) }
    val networkInfoBar = KView { withId(R.id.network_info) }
    val helpCardTitle = KTextView { withId(R.id.help_card_title) }
    val hamburgerButton =
        KButton { withContentDescription(R.string.nav_app_bar_open_drawer_description) }
    val settingsAction = KTextView { withText(R.string.settings) }
    val navDrawer = KNavigationView { withId(R.id.nav_view) }
    val drawerLayout = KDrawerView { withId(R.id.drawer_layout) }
    val radarFragment = KView { withContentDescription(R.string.radar) }
    val statsFragment = KView { withContentDescription(R.string.stats) }
}