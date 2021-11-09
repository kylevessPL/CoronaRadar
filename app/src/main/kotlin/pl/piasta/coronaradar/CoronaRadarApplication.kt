package pl.piasta.coronaradar

import android.content.Context
import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import com.akexorcist.localizationactivity.ui.LocalizationApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class CoronaRadarApplication : LocalizationApplication() {

    private val supportedLocales: Set<String> = setOf("en", "pl")

    override fun getDefaultLanguage(base: Context): Locale =
        ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0).language.takeIf {
            it in supportedLocales
        }?.let { language ->
            Locale.forLanguageTag(language)
        } ?: Locale.ENGLISH

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )
    }
}