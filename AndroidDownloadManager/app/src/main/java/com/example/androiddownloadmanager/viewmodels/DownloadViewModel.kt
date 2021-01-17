package com.example.androiddownloadmanager.viewmodels

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
        //save this info to database with none state (with none state it start automatically)
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

     fun updateAll() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                for (i in downloadList.value ?: listOf()) {
                    data.update(i)
                }
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

