package com.qnbfinansbank.deviceinfoapp

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.TrafficStats

/**
 * Created by yasin on 21.03.2018.
 */
class ApplicationItem(_app:ApplicationInfo) {

    val app = _app

    private var tx: Long = 0
    private var rx: Long = 0

    private var wifi_tx: Long = 0
    private var wifi_rx: Long = 0

    private var mobil_tx: Long = 0
    private var mobil_rx: Long = 0

    private var current_tx: Long = 0
    private var current_rx: Long = 0


    private var isMobil = false

    init  {
        update()
    }

    fun update() {
        val delta_tx = TrafficStats.getUidTxBytes(app.uid) - tx
        val delta_rx = TrafficStats.getUidRxBytes(app.uid) - rx

        tx = TrafficStats.getUidTxBytes(app.uid)
        rx = TrafficStats.getUidRxBytes(app.uid)

        current_tx = current_tx + delta_tx
        current_rx = current_rx + delta_rx

        if (isMobil == true) {
            mobil_tx = mobil_tx + delta_tx
            mobil_rx = mobil_rx + delta_rx
        } else {
            wifi_tx = wifi_tx + delta_tx
            wifi_rx = wifi_rx + delta_rx
        }
    }

    companion object {
        fun create(_app: ApplicationInfo): ApplicationItem? {
            val _tx = TrafficStats.getUidTxBytes(_app.uid)
            val _rx = TrafficStats.getUidRxBytes(_app.uid)

            return if (_tx + _rx > 0) ApplicationItem(_app) else null
        }
    }

    fun getTotalUsageKb(): Int {
        return Math.round(((tx + rx) / 1024).toFloat())
    }

    fun getApplicationLabel(_packageManager: PackageManager): String {
        return _packageManager.getApplicationLabel(app).toString()
    }

    fun getIcon(_packageManager: PackageManager): Drawable {
        return _packageManager.getApplicationIcon(app)
    }

    fun setMobilTraffic(_isMobil: Boolean) {
        isMobil = _isMobil
    }

}