package com.example.androiddownloadmanager.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.androiddownloadmanager.*
import com.example.androiddownloadmanager.R
import com.example.androiddownloadmanager.database.DownloadInfo
import com.example.androiddownloadmanager.databinding.DownloadViewBinding
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import java.io.File

class DownloadView : LinearLayout {

    private val onChangeListeners = ArrayList<OnStateChange>()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)


    //initializing view and connect to xml via Data Binding
    private val binding: DownloadViewBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.download_view,
        this,
        true
    )
    var info: DownloadInfo? = null

    fun setDownloadInformation(info: DownloadInfo) {
        if (this.info == null) {
            this.info = info
            setup()
        }
    }

    private var downloader: Fetch? = null


    private fun setup() {
        info?.let {

            //setup downloader
            val fetchConfig = FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(3)
                .build()
            downloader = Fetch.Impl.getInstance(fetchConfig)

            //setup download listener
            downloader?.addListener(downloadListener())

            //set up views with info
            binding.dlTxtName.text = it.name
            binding.dlTxtSize.text = getSize(it.size)
            binding.dlTxtSpeed.text = ""
            binding.dlTxtTime.text = ""
            when (it.state) {
                DownloadState.STOP -> binding.circleProgress.progress =
                    (File(it.path).length() / it.size * 100).toInt()
                DownloadState.ERROR -> binding.circleProgress.progress =
                    (File(it.path).length() / it.size * 100).toInt()
                DownloadState.SUCCESSFUL ->
                    binding.circleProgress.progress = 100
                DownloadState.INIT ->
                    binding.circleProgress.progress = 0
            }


            //on click for interact with user
            binding.circleProgress.setOnClickListener { _ ->
                when (it.state) {
                    DownloadState.INIT -> start()
                    DownloadState.RUNNING -> stop()
                    DownloadState.STOP -> resume()
                    DownloadState.ERROR -> start()

                }

            }
        }
    }

    private fun start() {
        info?.let {
            it.dId = download()
        }
    }

    private fun download(): Int {
        val request = Request(info!!.url, "${info!!.path}/${info!!.name}")
        downloader?.enqueue(request)
        return request.id
    }

    private fun downloadListener() = object : FetchListener {
        override fun onAdded(download: Download) {
        }

        override fun onCancelled(download: Download) {
        }

        override fun onCompleted(download: Download) {
            if (download.id == info?.dId)
                updateState(DownloadState.SUCCESSFUL)
        }

        override fun onDeleted(download: Download) {
        }

        override fun onDownloadBlockUpdated(
            download: Download,
            downloadBlock: DownloadBlock,
            totalBlocks: Int
        ) {
        }

        override fun onError(download: Download, error: Error, throwable: Throwable?) {
            if (download.id == info?.dId) {
                updateState(DownloadState.ERROR)
                Toast.makeText(context, "error : ${error.httpResponse}", Toast.LENGTH_SHORT).show()
            }

        }

        override fun onPaused(download: Download) {
            if (download.id == info?.dId)
                updateState(DownloadState.STOP)

        }

        override fun onProgress(
            download: Download,
            etaInMilliSeconds: Long,
            downloadedBytesPerSecond: Long
        ) {
            if (download.id == info?.dId) {
                binding.circleProgress.progress = download.progress
                binding.dlTxtSpeed.text = etaInMilliSeconds.toString()
                binding.dlTxtTime.text = downloadedBytesPerSecond.toString()
            }
        }

        override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        }

        override fun onRemoved(download: Download) {
        }

        override fun onResumed(download: Download) {
            if (download.id == info?.dId)
                updateState(DownloadState.RUNNING)

        }

        override fun onStarted(
            download: Download,
            downloadBlocks: List<DownloadBlock>,
            totalBlocks: Int
        ) {
            if (download.id == info?.dId) {
                updateState(DownloadState.RUNNING)
                binding.dlTxtSize.text = getSize(info!!.size)
            }
        }

        override fun onWaitingNetwork(download: Download) {
        }
    }

    private fun stop() {
        info?.dId?.let {
            downloader?.pause(it)
        }
    }

    private fun resume() {
        info?.dId?.let {
            downloader?.resume(it)
        }
    }

    private fun updateState(state: DownloadState) {
        info?.let {
            it.state = state
            for (i in onChangeListeners)
                i.onUpdate(state)
        }
    }

    fun setOnStateChangeListener(onChange: OnStateChange) {
        onChangeListeners.add(onChange)
    }

    override fun toString(): String {
        return info?.name ?: "null"
    }

    fun delete() {
        info?.let {
            downloader?.delete(it.dId ?: 0)
        }
    }

}