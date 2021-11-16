package pl.piasta.coronaradar.ui.stats.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.data.survey.model.Survey
import pl.piasta.coronaradar.ui.base.BaseAdapter
import pl.piasta.coronaradar.ui.common.adapter.RecyclerViewClickListener

class StatsAdapter(private val itemClickListener: RecyclerViewClickListener<Survey>) :
    BaseAdapter<Survey>(Companion) {

    override fun getLayoutIdForPosition(position: Int) = R.layout.item_survey

    fun onItemClicked(view: View, item: Survey) =
        itemClickListener.onRecyclerViewItemClick(view, item)

    companion object : ItemCallback<Survey>() {

        override fun areItemsTheSame(oldItem: Survey, newItem: Survey): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Survey, newItem: Survey): Boolean {
            return oldItem == newItem
        }
    }
}