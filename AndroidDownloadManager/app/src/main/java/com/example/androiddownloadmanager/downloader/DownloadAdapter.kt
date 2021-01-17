package com.example.androiddownloadmanager.downloader

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androiddownloadmanager.database.DownloadInfo
import com.example.androiddownloadmanager.DownloadState
import com.example.androiddownloadmanager.databinding.DownloadListItemBinding
import com.example.androiddownloadmanager.utility.getStateFromDb
import com.example.androiddownloadmanager.utility.setStateToDb

class DownloadAdapter : ListAdapter<DownloadInfo, DownloadViewHolder>(DownloadUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        return DownloadViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DownloadViewHolder(private val binding: DownloadListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(info: DownloadInfo) {
        binding.info = info
        binding.executePendingBindings()
        binding.downloadView.setDownloadInformation(info)
        if (getStateFromDb(info.state) == DownloadState.NONE)
            binding.downloadView.start()


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
}

class DownloadUtil : DiffUtil.ItemCallback<DownloadInfo>() {
    override fun areItemsTheSame(oldItem: DownloadInfo, newItem: DownloadInfo): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: DownloadInfo, newItem: DownloadInfo): Boolean {
        return oldItem == newItem
    }
}