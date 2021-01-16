package com.example.androiddownloadmanager.downloader

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androiddownloadmanager.DownloadInfo

class DownloadViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private val downloadList = mutableListOf<DownloadInfo>()

    fun addDownloadInfo(info: DownloadInfo, onUpdate: (List<DownloadInfo>) -> Unit) {
        downloadList.add(info)
        onUpdate(downloadList)
    }

    fun removeDownloadInfo(info: DownloadInfo) {
        downloadList.remove(info)
    }

    fun getNames(): Array<String> {
        val a = arrayListOf<String>()
        for (i in downloadList)
            a.add(i.name)

        return a.toTypedArray()
    }
}

