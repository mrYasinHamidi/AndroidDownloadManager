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
        val newInfo = info.copy()
        binding.info = newInfo
        binding.circleProgress.progress = newInfo.progress

        binding.circleProgress.setOnClickListener {
            when (newInfo.state) {
                DownloadState.INIT -> {
                    newInfo.dId = downloader.start(newInfo)
                    infoUpdate.onUpdate(newInfo)
                }
                DownloadState.RUNNING -> downloader.pause(newInfo)
                DownloadState.STOP -> downloader.resume(newInfo)
                DownloadState.ERROR -> downloader.retry(newInfo)
            }
        }
        addListiner(newInfo, downloader, infoUpdate)
        binding.executePendingBindings()

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
                    stateUpdate(infoUpdate, info)
                }

                override fun onError(download: Download, error: Error, throwable: Throwable?) {
                    info.state = DownloadState.ERROR
                    stateUpdate(infoUpdate, info)
                }

                override fun onDownloadBlockUpdated(
                    download: Download,
                    downloadBlock: DownloadBlock,
                    totalBlocks: Int
                ) {
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
                    binding.dlTxtSpeed.text = "${getSize(downloadedBytesPerSecond)}/s"
                    binding.dlTxtTime.text = "${(etaInMilliSeconds / 1000)}s"
                }

                override fun onPaused(download: Download) {
                    info.state = DownloadState.STOP
                    stateUpdate(infoUpdate, info)

                }

                override fun onResumed(download: Download) {
                    info.state = DownloadState.RUNNING
                    stateUpdate(infoUpdate, info)

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

    private fun stateUpdate(infoUpdate: InfoUpdate, info: DownloadInfo) {
        info.progress = binding.circleProgress.progress
        info.speed = binding.dlTxtSpeed.text.toString()
        info.timer = binding.dlTxtTime.text.toString()
        infoUpdate.onUpdate(info)
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