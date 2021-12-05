package pl.piasta.coronaradar.ui.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import pl.piasta.coronaradar.BR

abstract class BaseDialogFragment<DB : ViewDataBinding, VM : ViewModel?>(@LayoutRes private val layoutRes: Int) :
    DialogFragment() {

    private var _binding: DB? = null

    protected val binding get() = _binding!!

    protected open val dialogTheme = theme

    protected open val viewModel: VM? get() = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, dialogTheme)
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        updateUI()
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        viewModel?.let {
            binding.setVariable(BR.viewModel, viewModel)
        }
        binding.lifecycleOwner = viewLifecycleOwner
    }

    @CallSuper
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        doOnDismiss()
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cleanup()
    }

    protected open fun setupView() {}

    protected open fun updateUI() {}

    protected open fun doOnDismiss() {}

    protected open fun cleanup() {}
}