package pl.piasta.coronaradar.ui.common.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData

class LiveBroadcast(private val ctx: Context, private val filters: List<String>) :
    LiveData<Intent>() {

    private val intentFilter = IntentFilter().apply {
        filters.forEach {
            addAction(it)
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            postValue(intent)
        }
    }

    override fun onActive() {
        super.onActive()
        ctx.registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onInactive() {
        super.onInactive()
        ctx.unregisterReceiver(broadcastReceiver)
    }
}