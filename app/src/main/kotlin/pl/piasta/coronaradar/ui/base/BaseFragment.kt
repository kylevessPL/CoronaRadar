package pl.piasta.coronaradar.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import pl.piasta.coronaradar.BR
import splitties.resources.str

abstract class BaseFragment<DB : ViewDataBinding, VM : ViewModel?>(@LayoutRes private val layoutRes: Int) :
    Fragment() {

    private var _binding: DB? = null

    protected val binding get() = _binding!!
    protected open val viewModel: VM? get() = null

    @StringRes
    protected open val title: Int? = null

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.let {
            binding.setVariable(BR.viewModel, viewModel)
        }
        binding.lifecycleOwner = viewLifecycleOwner
        title?.let {
            (requireActivity() as AppCompatActivity).title = str(it)
        }
        setupView()
        updateUI()
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cleanup()
    }

    protected open fun setupView() {}

    protected open fun updateUI() {}

    protected open fun cleanup() {}
}