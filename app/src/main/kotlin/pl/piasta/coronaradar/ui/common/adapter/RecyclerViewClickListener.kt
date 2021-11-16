package pl.piasta.coronaradar.ui.common.adapter

import android.view.View

interface RecyclerViewClickListener<T> {
    fun onRecyclerViewItemClick(view: View, item: T)
}