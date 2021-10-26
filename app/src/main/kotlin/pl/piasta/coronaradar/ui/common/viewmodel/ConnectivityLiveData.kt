package pl.piasta.coronaradar.ui.common.viewmodel

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData

class ConnectivityLiveData @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE) constructor(
    private val ctx: Context
) : LiveData<Boolean>() {

    private val connectivityManager by lazy { ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            postValue(true)
        }

        override fun onLost(network: Network) {
            postValue(false)
        }
    }

    init {
        init()
    }

    override fun onActive() {
        super.onActive()
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                .build(),
            networkCallback
        )
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun init() {
        value = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            ?.let { network ->
                network.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    network.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            } ?: false
    }
}