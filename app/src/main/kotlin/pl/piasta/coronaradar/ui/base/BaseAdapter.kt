package pl.piasta.coronaradar.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<Item : Any, DB : ViewDataBinding>(callback: ItemCallback<Item>) :
    PagingDataAdapter<Item, BaseAdapter<Item, DB>.BaseViewHolder>(callback) {

    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<DB>(inflater, viewType, parent, false)
        return BaseViewHolder(binding)
    }

    @CallSuper
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class BaseViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setVariable(BR.adapter, this@BaseAdapter)
            binding.executePendingBindings()
        }

        fun bind(item: Item) = binding.setVariable(BR.item, item)
    }
}