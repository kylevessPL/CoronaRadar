package pl.piasta.coronaradar.ui.base

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.ui.util.expandDialog
import pl.piasta.coronaradar.util.ifTrue

abstract class BaseBottomSheetDialogFragment<DB : ViewDataBinding, VM : ViewModel?>(@LayoutRes private val layoutRes: Int) :
    BottomSheetDialogFragment() {

    protected open val dialogTheme = theme

    private var _binding: DB? = null

    protected val binding get() = _binding!!
    protected open val viewModel: VM? get() = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, dialogTheme)
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

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        (resources.configuration.orientation == ORIENTATION_LANDSCAPE).ifTrue {
            expandDialog()
        }
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected open fun setupView() {}

    protected open fun updateUI() {}
}