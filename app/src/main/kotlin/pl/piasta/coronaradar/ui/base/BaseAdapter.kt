package pl.piasta.coronaradar.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import pl.piasta.coronaradar.BR

abstract class BaseAdapter<T : Any>(callback: ItemCallback<T>) :
    PagingDataAdapter<T, BaseAdapter<T>.BaseViewHolder>(callback) {

    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        return BaseViewHolder(binding)
    }

    @CallSuper
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    @CallSuper
    override fun getItemViewType(position: Int) = getLayoutIdForPosition(position)

    @LayoutRes
    protected abstract fun getLayoutIdForPosition(position: Int): Int

    inner class BaseViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setVariable(BR.adapter, this@BaseAdapter)
            binding.executePendingBindings()
        }

        fun bind(item: T) {
            binding.setVariable(BR.item, item)
        }
    }
}