package pl.piasta.coronaradar.ui.base

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

abstract class BaseCustomViewBottomSheetDialogFragment<DB : ViewDataBinding, PVM : ViewModel?>(@LayoutRes private val layoutRes: Int) :
    BottomSheetDialogFragment() {

    private var _binding: DB? = null

    protected val binding get() = _binding!!
    protected open val parentViewModel: PVM? get() = null

    @CallSuper
    override fun onStart() {
        super.onStart()
        parentViewModel?.let {
            binding.setVariable(BR.viewModel, parentViewModel)
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

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected open fun setupView() {}

    protected open fun updateUI() {}
}