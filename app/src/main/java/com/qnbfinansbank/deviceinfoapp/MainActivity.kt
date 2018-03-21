package com.qnbfinansbank.deviceinfoapp

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.TrafficStats
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ListView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var tvSupported: TextView? = null
    private var tvDataUsageWiFi:TextView? = null
    private var tvDataUsageMobile:TextView? = null
    private var tvDataUsageTotal:TextView? = null
    private var lvApplications: ListView? = null

    private var dataUsageTotalLast: Long = 0

    internal lateinit var adapterApplications: ArrayAdapter<ApplicationItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvSupported = findViewById<View>(R.id.tvSupported) as TextView
        tvDataUsageWiFi = findViewById<View>(R.id.tvDataUsageWiFi) as TextView
        tvDataUsageMobile = findViewById<View>(R.id.tvDataUsageMobile) as TextView
        tvDataUsageTotal = findViewById<View>(R.id.tvDataUsageTotal) as TextView

        if (TrafficStats.getTotalRxBytes() != TrafficStats.UNSUPPORTED.toLong() && TrafficStats.getTotalTxBytes() != TrafficStats.UNSUPPORTED.toLong()) {
            handler.postDelayed(runnable, 0)


            initAdapter()
            lvApplications = findViewById<View>(R.id.lvInstallApplication) as ListView
            lvApplications!!.setAdapter(adapterApplications)
        } else {
            tvSupported!!.setVisibility(View.VISIBLE)
        }
    }

    var handler = Handler()
    var runnable: Runnable = object : Runnable {
        override fun run() {
            val mobile = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes()
            val total = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()
            tvDataUsageWiFi!!.setText("" + (total - mobile) / 1024 + " Kb")
            tvDataUsageMobile!!.setText("" + mobile / 1024 + " Kb")
            tvDataUsageTotal!!.setText("" + total / 1024 + " Kb")
            if (dataUsageTotalLast != total) {
                dataUsageTotalLast = total
                updateAdapter()
            }
            handler.postDelayed(this, 5000)
        }
    }

    fun initAdapter() {

        adapterApplications = object : ArrayAdapter<ApplicationItem>(applicationContext, R.layout.item_install_application) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val app = getItem(position)

                val result: View?
                if (convertView == null) {
                    result = LayoutInflater.from(parent.context).inflate(R.layout.item_install_application, parent, false)
                } else {
                    result = convertView
                }

                val tvAppName = result!!.findViewById<View>(R.id.tvAppName) as TextView
                val tvAppTraffic = result.findViewById<View>(R.id.tvAppTraffic) as TextView

                // TODO: resize once
                val iconSize = Math.round(32 * resources.displayMetrics.density)
                tvAppName.setCompoundDrawablesWithIntrinsicBounds(
                        //app.icon,
                        BitmapDrawable(resources, Bitmap.createScaledBitmap(
                                (app!!.getIcon(applicationContext.packageManager) as BitmapDrawable).bitmap, iconSize, iconSize, true)
                        ), null, null, null
                )
                tvAppName.text = app.getApplicationLabel(applicationContext.packageManager)
                tvAppTraffic.text = Integer.toString(app.getTotalUsageKb()) + " Kb"

                return result
            }

            override fun getCount(): Int {
                return super.getCount()
            }

            override fun getFilter(): Filter {
                return super.getFilter()
            }
        }

        // TODO: resize icon once
        for (app in applicationContext.packageManager.getInstalledApplications(0)) {
            val item = ApplicationItem.create(app)
            if (item != null) {
                adapterApplications.add(item)
            }
        }
    }

    fun updateAdapter() {
        var i = 0
        val l = adapterApplications.count
        while (i < l) {
            val app = adapterApplications.getItem(i)
            app!!.update()
            i++
        }

        adapterApplications.sort { lhs, rhs -> rhs.getTotalUsageKb() - lhs.getTotalUsageKb() }
        adapterApplications.notifyDataSetChanged()
    }
}
