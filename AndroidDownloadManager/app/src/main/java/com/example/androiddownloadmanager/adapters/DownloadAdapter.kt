package com.example.androiddownloadmanager.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androiddownloadmanager.DownloadState
import com.example.androiddownloadmanager.Downloader
import com.example.androiddownloadmanager.RecyclerViewCallback
import com.example.androiddownloadmanager.database.DownloadInfo
import com.example.androiddownloadmanager.databinding.DownloadListItemBinding
import com.example.androiddownloadmanager.getSize
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2core.DownloadBlock

class DownloadAdapter(private val downloader: Downloader, private val infoUpdate: InfoUpdate) :
    ListAdapter<DownloadInfo, DownloadViewHolder>(DownloadUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        return DownloadViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.bind(getItem(position), downloader, infoUpdate)
        Log.i("aaa","item bind position : $position")
    }
}

class DownloadViewHolder(private val binding: DownloadListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(info: DownloadInfo, downloader: Downloader, infoUpdate: InfoUpdate) {
        binding.executePendingBindings()

        binding.circleProgress.setOnClickListener {
            when(info.state){
                DownloadState.INIT -> {
                    info.dId = downloader.start(info)
                    infoUpdate.onUpdate(info)
                }
                DownloadState.RUNNING -> downloader.pause(info)
                DownloadState.STOP -> downloader.resume(info)
                DownloadState.ERROR -> downloader.retry(info)
            }
        }
        addListiner(info, downloader, infoUpdate)
    }


    companion object {
        fun from(parent: ViewGroup): DownloadViewHolder =
            DownloadViewHolder(
                DownloadListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )


    }

    private fun addListiner(info: DownloadInfo, downloader: Downloader, infoUpdate: InfoUpdate) {
        info.dId?.let {
            downloader.addListiner(it, object : RecyclerViewCallback {
                override fun onAdded(download: Download) {
                    binding.dlTxtName.text = "Initializing"
                }

                override fun onQueued(download: Download, waitingOnNetwork: Boolean) {

                }

                override fun onWaitingNetwork(download: Download) {
                    binding.dlTxtName.text = "Waiting for network"
                }

                override fun onCompleted(download: Download) {
                    info.state = DownloadState.SUCCESSFUL
                    infoUpdate.onUpdate(info)
                }

                override fun onError(download: Download, error: Error, throwable: Throwable?) {
                    info.state = DownloadState.ERROR
                    infoUpdate.onUpdate(info)
                }

                override fun onDownloadBlockUpdated(
                    download: Download,
                    downloadBlock: DownloadBlock,
                    totalBlocks: Int
                ) {
                    Log.i("aaa","downloadBlock : $downloadBlock \t totalBlock : $totalBlocks")
                }

                override fun onStarted(
                    download: Download,
                    downloadBlocks: List<DownloadBlock>,
                    totalBlocks: Int
                ) {
                    binding.dlTxtName.text = info.name
                    info.state = DownloadState.RUNNING
                    infoUpdate.onUpdate(info)
                }

                override fun onProgress(
                    download: Download,
                    etaInMilliSeconds: Long,
                    downloadedBytesPerSecond: Long
                ) {
                    binding.circleProgress.progress = download.progress
                    binding.dlTxtSpeed.text = getSize(downloadedBytesPerSecond)
                    binding.dlTxtTime.text = (etaInMilliSeconds / 1000).toString()
                }

                override fun onPaused(download: Download) {
                    info.state = DownloadState.STOP
                    infoUpdate.onUpdate(info)
                }

                override fun onResumed(download: Download) {
                    info.state = DownloadState.RUNNING
                    infoUpdate.onUpdate(info)
                }

                override fun onCancelled(download: Download) {
                }

                override fun onRemoved(download: Download) {
                }

                override fun onDeleted(download: Download) {

                }
            })
        }
    }
}

class DownloadUtil : DiffUtil.ItemCallback<DownloadInfo>() {
    override fun areItemsTheSame(oldItem: DownloadInfo, newItem: DownloadInfo): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: DownloadInfo, newItem: DownloadInfo): Boolean {
        return oldItem == newItem
    }
}

class InfoUpdate(private val listiner: (DownloadInfo) -> Unit) {
    fun onUpdate(info: DownloadInfo) = listiner(info)
}

//https://project.yasinhamidi.ir/MafiaApp/mafia.apk