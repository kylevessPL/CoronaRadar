package pl.piasta.coronaradar.ui.common.viewmodel

import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.*
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData

class ConnectivityLiveData @RequiresPermission(ACCESS_NETWORK_STATE) constructor(
    private val ctx: Context
) : LiveData<Boolean>() {

    private val connectivityManager by lazy { ctx.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            postValue(true)
        }

        override fun onLost(network: Network) {
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()
        setInitialState()
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder()
                .addCapability(NET_CAPABILITY_INTERNET)
                .addTransportType(TRANSPORT_CELLULAR)
                .addTransportType(TRANSPORT_WIFI)
                .addTransportType(TRANSPORT_ETHERNET)
                .addCapability(NET_CAPABILITY_VALIDATED)
                .build(),
            networkCallback
        )
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun setInitialState() {
        value = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            ?.let { network ->
                network.hasCapability(NET_CAPABILITY_INTERNET) &&
                        network.hasCapability(NET_CAPABILITY_VALIDATED) &&
                        network.hasTransport(TRANSPORT_CELLULAR) ||
                        network.hasTransport(TRANSPORT_WIFI) ||
                        network.hasTransport(TRANSPORT_ETHERNET)
            } ?: false
    }
}