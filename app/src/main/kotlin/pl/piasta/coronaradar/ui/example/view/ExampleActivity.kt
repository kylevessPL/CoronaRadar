package pl.piasta.coronaradar.ui.example.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import pl.piasta.coronaradar.databinding.ActivityExampleBinding

class ExampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Example text", Snackbar.LENGTH_LONG)
                .setAction("Example action", null).show()
        }
    }
}