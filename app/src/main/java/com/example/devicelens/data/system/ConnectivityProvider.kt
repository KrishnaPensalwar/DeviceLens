package com.example.devicelens.data.system

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import com.example.devicelens.domain.model.ConnectionType
import com.example.devicelens.domain.model.NetworkInfo
import com.example.devicelens.domain.model.SignalQuality
import com.example.devicelens.domain.model.calculateNetworkHealth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.URL
import javax.inject.Inject

class ConnectivityProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    suspend fun getNetworkInfo(): NetworkInfo = withContext(Dispatchers.IO) {
        snapshot()
    }

    suspend fun runNetworkTest(): NetworkInfo = withContext(Dispatchers.IO) {
        val snapshot = snapshot()
        if (!snapshot.isInternetAvailable) {
            return@withContext snapshot.copy(
                qualityScore = calculateNetworkHealth(
                    internetAvailable = false,
                    signalQuality = snapshot.signalQuality,
                    latencyMs = null,
                    downloadMbps = null
                ).score
            )
        }

        val latencyMs = measureLatencyMs()
        val downloadMbps = measureDownloadMbps()
        val uploadMbps = measureUploadMbps()
        val health = calculateNetworkHealth(
            internetAvailable = true,
            signalQuality = snapshot.signalQuality,
            latencyMs = latencyMs,
            downloadMbps = downloadMbps
        )

        snapshot.copy(
            downloadMbps = downloadMbps,
            uploadMbps = uploadMbps,
            latencyMs = latencyMs,
            qualityScore = health.score
        )
    }

    private fun snapshot(): NetworkInfo {
        val network = connectivityManager.activeNetwork
        if (network == null) {
            return NetworkInfo(
                connectionType = ConnectionType.NONE,
                isInternetAvailable = false
            )
        }

        val capabilities = connectivityManager.getNetworkCapabilities(network)
        if (capabilities == null) {
            return NetworkInfo(
                connectionType = ConnectionType.NONE,
                isInternetAvailable = false
            )
        }

        val connectionType = when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->
                ConnectionType.WIFI

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->
                ConnectionType.MOBILE

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->
                ConnectionType.ETHERNET

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ->
                ConnectionType.VPN

            else -> ConnectionType.UNKNOWN
        }

        val internetAvailable =
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        val wifiInfo =
            if (connectionType == ConnectionType.WIFI) getWifiInfo() else null

        return NetworkInfo(
            connectionType = connectionType,
            networkName = wifiInfo?.ssid,
            isInternetAvailable = internetAvailable,
            signalDbm = wifiInfo?.rssi,
            signalQuality = wifiInfo?.quality ?: SignalQuality.UNKNOWN,
            ipAddress = getIpAddress()
        )
    }

    private fun getWifiInfo(): WifiResult? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val network = connectivityManager.activeNetwork ?: return null
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return null
            val wifiInfo =
                capabilities.transportInfo as? android.net.wifi.WifiInfo ?: return null
            val rssi = wifiInfo.rssi
            return WifiResult(
                ssid = wifiInfo.ssid.removeSurrounding("\""),
                rssi = rssi,
                quality = getSignalQuality(rssi)
            )
        }

        @Suppress("DEPRECATION")
        val wifiInfo = wifiManager.connectionInfo
        val rssi = wifiInfo.rssi
        return WifiResult(
            ssid = wifiInfo.ssid.removeSurrounding("\""),
            rssi = rssi,
            quality = getSignalQuality(rssi)
        )
    }

    private fun getSignalQuality(rssi: Int): SignalQuality {
        return when {
            rssi >= -55 -> SignalQuality.EXCELLENT
            rssi >= -67 -> SignalQuality.GOOD
            rssi >= -75 -> SignalQuality.FAIR
            else -> SignalQuality.POOR
        }
    }

    private fun getIpAddress(): String? {
        return try {
            NetworkInterface
                .getNetworkInterfaces()
                .toList()
                .flatMap { networkInterface ->
                    networkInterface.inetAddresses.toList()
                }
                .firstOrNull { address ->
                    !address.isLoopbackAddress && address is Inet4Address
                }
                ?.hostAddress
        } catch (_: Exception) {
            null
        }
    }

    private fun measureLatencyMs(): Long? {
        return try {
            val start = System.currentTimeMillis()
            Socket().use { socket ->
                socket.connect(InetSocketAddress("1.1.1.1", 443), 3_000)
            }
            System.currentTimeMillis() - start
        } catch (_: Exception) {
            null
        }
    }

    private fun measureDownloadMbps(): Double? {
        return try {
            val url = URL("https://speed.cloudflare.com/__down?bytes=200000")
            val connection = url.openConnection()
            connection.connectTimeout = 5_000
            connection.readTimeout = 8_000
            val start = System.nanoTime()
            val bytes = connection.getInputStream().use { it.readBytes().size }
            val elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0
            if (elapsedSeconds <= 0.0 || bytes <= 0) null
            else (bytes * 8.0) / elapsedSeconds / 1_000_000.0
        } catch (_: Exception) {
            null
        }
    }

    private fun measureUploadMbps(): Double? {
        return try {
            val payload = ByteArray(200_000)
            val connection = URL("https://speed.cloudflare.com/__up").openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.connectTimeout = 5_000
            connection.readTimeout = 8_000
            connection.setFixedLengthStreamingMode(payload.size)
            val start = System.nanoTime()
            connection.outputStream.use { it.write(payload) }
            connection.responseCode
            val elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0
            connection.disconnect()
            if (elapsedSeconds <= 0.0) null
            else (payload.size * 8.0) / elapsedSeconds / 1_000_000.0
        } catch (_: Exception) {
            null
        }
    }

    private data class WifiResult(
        val ssid: String?,
        val rssi: Int,
        val quality: SignalQuality
    )
}
