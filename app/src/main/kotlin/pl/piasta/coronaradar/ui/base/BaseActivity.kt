package pl.piasta.coronaradar.ui.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import pl.piasta.coronaradar.BR

abstract class BaseActivity<DB : ViewDataBinding, VM : ViewModel?>(@LayoutRes private val layoutRes: Int) :
    LocalizationActivity() {

    protected lateinit var binding: DB
        private set
    protected open val viewModel: VM? get() = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
        viewModel?.let {
            binding.setVariable(BR.viewModel, it)
        }
        binding.lifecycleOwner = this
        setupView()
        updateUI()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        cleanup()
    }

    protected open fun setupView() {}

    protected open fun updateUI() {}

    protected open fun cleanup() {}
}