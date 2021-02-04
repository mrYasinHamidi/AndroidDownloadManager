package com.example.androiddownloadmanager

import android.util.Log
import com.example.androiddownloadmanager.database.DownloadInfo
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2.Request
import com.tonyodev.fetch2core.DownloadBlock

class Downloader(private val fetch: Fetch) {

    private val listeners: MutableMap<Int, RecyclerViewCallback> = mutableMapOf()

    init {
        fetch.addListener(
            object : FetchListener {
                override fun onAdded(download: Download) {
                    listeners[download.id]?.onAdded(download)
                }

                override fun onCancelled(download: Download) {
                    listeners[download.id]?.onCancelled(download)
                }

                override fun onCompleted(download: Download) {
                    listeners[download.id]?.onCompleted(download)
                }

                override fun onDeleted(download: Download) {
                    listeners[download.id]?.onDeleted(download)
                }

                override fun onDownloadBlockUpdated(
                    download: Download,
                    downloadBlock: DownloadBlock,
                    totalBlocks: Int
                ) {
                    listeners[download.id]?.onDownloadBlockUpdated(
                        download,
                        downloadBlock,
                        totalBlocks
                    )
                }

                override fun onError(download: Download, error: Error, throwable: Throwable?) {
                    listeners[download.id]?.onError(download, error, throwable)
                }

                override fun onPaused(download: Download) {
                    listeners[download.id]?.onPaused(download)
                }

                override fun onProgress(
                    download: Download,
                    etaInMilliSeconds: Long,
                    downloadedBytesPerSecond: Long
                ) {
                    listeners[download.id]?.onProgress(
                        download,
                        etaInMilliSeconds,
                        downloadedBytesPerSecond
                    )
                }

                override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
                    listeners[download.id]?.onQueued(download, waitingOnNetwork)
                }

                override fun onRemoved(download: Download) {
                    listeners[download.id]?.onRemoved(download)
                }

                override fun onResumed(download: Download) {
                    listeners[download.id]?.onResumed(download)
                }

                override fun onStarted(
                    download: Download,
                    downloadBlocks: List<DownloadBlock>,
                    totalBlocks: Int
                ) {
                    listeners[download.id]?.onStarted(download, downloadBlocks, totalBlocks)
                }

                override fun onWaitingNetwork(download: Download) {
                    listeners[download.id]?.onWaitingNetwork(download)
                }

            }
        )
    }

    fun start(info: DownloadInfo):Int {
        val request = Request(info.url, "${info.path}/${info.name}")
        fetch.enqueue(request)
        return request.id
    }

    fun pause(info: DownloadInfo) {
        info.dId?.let {
            fetch.pause(it)
        }
    }

    fun resume(info: DownloadInfo) {
        info.dId?.let {
            fetch.resume(it)
        }
    }

    fun cancel(info: DownloadInfo) {
        info.dId?.let {
            fetch.cancel(it)
        }
    }

    fun retry(info: DownloadInfo) {
        info.dId?.let {
            fetch.retry(it)
        }
    }

    fun addListiner(id: Int, callback: RecyclerViewCallback) {
        if (listeners[id] == null) {
            listeners[id] = callback
        }

    }

}