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
            binding.setVariable(BR.viewModel, viewModel)
        }
        binding.lifecycleOwner = this
        setupActionBar()
        setupNavController()
        setupNavDrawer()
        updateUI()
    }

    protected open fun setupActionBar() {}

    protected open fun setupNavDrawer() {}

    protected open fun setupNavController() {}

    protected open fun updateUI() {}
}