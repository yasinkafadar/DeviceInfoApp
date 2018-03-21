package com.qnbfinansbank.deviceinfoapp

import android.content.Context
import android.net.ConnectivityManager
import java.util.*

/**
 * Created by yasin on 21.03.2018.
 */
class ApplicationList(_context: Context) {
    private val mContext: Context
    private var mTimer: Timer? = null
    private var mTask: TimerTask? = null

    private val TIME_APPLICATION_UPDATE = 3 * 1000 // 3 second

    private var isWifiEnabled = false
    private var isMobilEnabled = false

    private val mApplicationItemList = ArrayList<ApplicationItem>()

    init {
        mContext = _context
    }

    fun Start() {
        mTask = object : TimerTask() {
            override fun run() {
                update()
            }
        }

        mTimer = Timer()
        mTimer!!.schedule(mTask, 0, TIME_APPLICATION_UPDATE.toLong())
    }

    fun Stop() {
        if (mTimer != null) {
            mTimer!!.purge()
            mTimer!!.cancel()
        }
        if (mTask != null) {
            mTask!!.cancel()
            mTask = null
        }
    }

    fun update() {
        updateNetworkState()
        if (mApplicationItemList != null) {
            var i = 0
            val l = mApplicationItemList.size
            while (i < l) {
                mApplicationItemList[i].setMobilTraffic(isMobilEnabled)
                mApplicationItemList[i].update()
                i++
            }
        } else {
            for (app in mContext.packageManager.getInstalledApplications(0)) {
                val item = ApplicationItem(app)
                item.setMobilTraffic(isMobilEnabled)

                mApplicationItemList.add(item)
            }
        }
    }

    private fun updateNetworkState() {
        isWifiEnabled = isConnectedWifi()
        isMobilEnabled = isConnectedMobile()
    }

    fun getList(): List<ApplicationItem> {
        return mApplicationItemList
    }

    fun isConnectedWifi(): Boolean {
        val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info != null && info.isConnected && info.type == ConnectivityManager.TYPE_WIFI
    }

    fun isConnectedMobile(): Boolean {
        val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info != null && info.isConnected && info.type == ConnectivityManager.TYPE_MOBILE
    }
}