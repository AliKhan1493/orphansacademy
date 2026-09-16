package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class NetworkMonitor(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val _isDeviceConnected = MutableStateFlow(checkInitialConnectivity())
    private val _isSimulatedOffline = MutableStateFlow(false)

    val isSimulatedOffline: StateFlow<Boolean> = _isSimulatedOffline

    val isOnline: StateFlow<Boolean> = combine(_isDeviceConnected, _isSimulatedOffline) { connected, simulated ->
        connected && !simulated
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = _isDeviceConnected.value && !_isSimulatedOffline.value
    )

    init {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager?.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        _isDeviceConnected.value = true
                    }

                    override fun onLost(network: Network) {
                        _isDeviceConnected.value = checkInitialConnectivity()
                    }

                    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                        val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        _isDeviceConnected.value = hasInternet
                    }
                })
            } else {
                val request = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()

                connectivityManager?.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        _isDeviceConnected.value = true
                    }

                    override fun onLost(network: Network) {
                        _isDeviceConnected.value = false
                    }
                })
            }
        } catch (_: Exception) {
            _isDeviceConnected.value = true
        }
    }

    private fun checkInitialConnectivity(): Boolean {
        return try {
            val cm = connectivityManager ?: return true
            val activeNetwork = cm.activeNetwork
            if (activeNetwork != null) {
                val capabilities = cm.getNetworkCapabilities(activeNetwork)
                if (capabilities != null) {
                    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                }
            }
            @Suppress("DEPRECATION")
            val netInfo = cm.activeNetworkInfo
            if (netInfo != null) {
                return netInfo.isConnected
            }
            true // default to true so users with active internet are never falsely blocked
        } catch (_: Exception) {
            true
        }
    }

    fun toggleSimulatedOffline() {
        _isSimulatedOffline.value = !_isSimulatedOffline.value
    }

    fun setSimulatedOffline(offline: Boolean) {
        _isSimulatedOffline.value = offline
    }
}
