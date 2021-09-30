package pl.piasta.coronaradar.ui.main.view

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.BuildConfig
import pl.piasta.coronaradar.ui.main.viewmodel.MainViewModel
import pl.piasta.coronaradar.utils.ImageUtils
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
        Log.e("tag", BuildConfig.APPLICATION_ID)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            Handler().postDelayed({
                ImageUtils.generateSpectrogram(File("/sdcard/cough-heavy.wav"), applicationContext)
            }, 500)
        }
    }
}
