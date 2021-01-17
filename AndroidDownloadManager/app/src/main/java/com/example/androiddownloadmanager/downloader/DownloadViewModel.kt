package com.example.androiddownloadmanager.downloader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androiddownloadmanager.database.DownloadDao
import com.example.androiddownloadmanager.database.DownloadInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadViewModel(private val data: DownloadDao, app: Application) : AndroidViewModel(app) {
    // TODO: Implement the ViewModel
     val downloadList = data.getAllCrypto()

    fun addDownloadInfo(info: DownloadInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                data.insert(info)
            }
        }
    }

    fun removeDownloadInfo(info: DownloadInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                data.delete(info)
            }
        }
    }

    fun getNames(): Array<String> = downloadList.value?.let {
        val a = arrayListOf<String>()
        for (i in it)
            a.add(i.name)
        a.toTypedArray()
    } ?: arrayOf()

}

class DownloadViewModelFactory(private val data: DownloadDao, private val app: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadViewModel::class.java))
            return DownloadViewModel(data, app) as T
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}
