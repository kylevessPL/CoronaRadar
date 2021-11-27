package pl.piasta.coronaradar.ui.base

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.util.ifTrue

abstract class BaseFullScreenDialogFragment<DB : ViewDataBinding, PVM : ViewModel?>(
    @LayoutRes layoutRes: Int,
    @StringRes private val titleRes: Int
) : BaseDialogFragment<DB, PVM>(layoutRes) {

    protected open val actionBar: Toolbar? = null
    override val dialogTheme = R.style.Theme_CoronaRadar_Dialog_FullScreen

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) = menu.clear()

    @CallSuper
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        (item.itemId == android.R.id.home).ifTrue {
            dismiss()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @CallSuper
    override fun setupView() {
        setupFullScreenMode()
    }

    private fun setupFullScreenMode() {
        dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
        setHasOptionsMenu(true)
        with(requireActivity() as AppCompatActivity) {
            this@BaseFullScreenDialogFragment.actionBar?.let {
                setSupportActionBar(it)
                setTitle(titleRes)
                supportActionBar?.apply {
                    setDisplayHomeAsUpEnabled(true)
                    setHomeButtonEnabled(true)
                    setHomeAsUpIndicator(R.drawable.ic_close_24)
                }
            }
        }
    }
}