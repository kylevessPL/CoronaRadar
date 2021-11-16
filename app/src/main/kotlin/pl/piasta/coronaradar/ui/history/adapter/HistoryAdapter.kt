package pl.piasta.coronaradar.ui.history.adapter

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.history.model.History
import pl.piasta.coronaradar.ui.base.BaseAdapter

class HistoryAdapter : BaseAdapter<History>(Companion) {

    override fun getLayoutIdForPosition(position: Int) = R.layout.item_history

    companion object : ItemCallback<History>() {

        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem == newItem
        }
    }
}