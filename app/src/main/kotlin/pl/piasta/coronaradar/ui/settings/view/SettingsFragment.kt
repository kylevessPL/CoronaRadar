package pl.piasta.coronaradar.ui.settings.view

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.akexorcist.localizationactivity.core.LanguageSetting
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import splitties.resources.str
import java.util.*

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        findPreference<ListPreference>(str(R.string.language_key))?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, language ->
                (activity as? SettingsActivity)?.apply {
                    val locale = (language as String).takeIf { it != "default" }?.let {
                        Locale.forLanguageTag(language)
                    } ?: LanguageSetting.getDefaultLanguage(requireContext())
                    setLanguage(locale)
                }
                true
            }
    }
}