package pl.piasta.coronaradar

import android.content.Context
import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import com.akexorcist.localizationactivity.ui.LocalizationApplication
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp
import pl.piasta.coronaradar.util.supportedLocales
import java.util.*

@HiltAndroidApp
class CoronaRadarApplication : BaseApplication()

abstract class BaseApplication : LocalizationApplication() {

    override fun getDefaultLanguage(context: Context): Locale =
        ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0).takeIf {
            it in supportedLocales
        } ?: Locale.ENGLISH

    override fun onCreate() {
        super.onCreate()
        Python.start(AndroidPlatform(this))
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )
    }
}