package pl.piasta.coronaradar.ui.settings

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.ActivitySettingsBinding
import pl.piasta.coronaradar.ui.base.BaseActivity

@AndroidEntryPoint
class SettingsActivity :
    BaseActivity<ActivitySettingsBinding, Nothing>(R.layout.activity_settings) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}