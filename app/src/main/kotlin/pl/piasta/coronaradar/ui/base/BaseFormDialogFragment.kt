package pl.piasta.coronaradar.ui.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.MotionEvent
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import pl.piasta.coronaradar.BR
import pl.piasta.coronaradar.ui.util.dispatchActionDownTouchEvent
import splitties.alertdialog.appcompat.onShow
import splitties.alertdialog.appcompat.positiveButton
import splitties.resources.str

abstract class BaseFormDialogFragment<DB : ViewDataBinding, VM : ViewModel?, AVM : ViewModel?>(
    @LayoutRes private val layoutRes: Int,
    @StringRes private val titleRes: Int,
    @StringRes private val messageRes: Int
) : DialogFragment() {

    private var _viewBinding: DB? = null

    protected val viewBinding get() = _viewBinding!!
    protected open val viewModel: VM? get() = null
    protected open val activityViewModel: AVM? get() = null

    @StringRes
    protected open val positiveButtonRes: Int = android.R.string.ok

    @CallSuper
    override fun onStart() {
        super.onStart()
        viewModel?.let {
            viewBinding.setVariable(BR.viewModel, viewModel)
        }
        viewBinding.lifecycleOwner = this
        registerOnPropertyChangedCallback()
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        unregisterOnPropertyChangedCallback()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _viewBinding = DataBindingUtil.inflate(layoutInflater, layoutRes, null, false)
        return object : AlertDialog(requireContext(), theme) {

            override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                window?.dispatchActionDownTouchEvent(event)
                return super.dispatchTouchEvent(event)
            }
        }.apply {
            setView(viewBinding.root)
            setTitle(titleRes)
            setMessage(str(messageRes))
            setButton(
                DialogInterface.BUTTON_POSITIVE,
                str(positiveButtonRes)
            ) { _, _ -> onPositiveButtonClick() }
            setButton(DialogInterface.BUTTON_NEGATIVE, str(android.R.string.cancel)) { _, _ -> }
        }.onShow { positiveButton.isEnabled = false }
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    protected abstract fun onPositiveButtonClick()

    protected abstract fun registerOnPropertyChangedCallback()

    protected abstract fun unregisterOnPropertyChangedCallback()
}