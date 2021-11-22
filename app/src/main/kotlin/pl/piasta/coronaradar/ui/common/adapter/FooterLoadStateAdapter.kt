package pl.piasta.coronaradar.ui.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.piasta.coronaradar.databinding.LoadStateViewBinding
import pl.piasta.coronaradar.ui.common.adapter.FooterLoadStateAdapter.LoadStateViewHolder

class FooterLoadStateAdapter(val retry: () -> Unit) : LoadStateAdapter<LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LoadStateViewBinding.inflate(inflater, parent, false)
        return LoadStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) =
        holder.bind(loadState)

    inner class LoadStateViewHolder(private val binding: LoadStateViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.adapter = this@FooterLoadStateAdapter
        }

        fun bind(loadState: LoadState) {
            binding.loadState = loadState
            binding.executePendingBindings()
        }
    }
}