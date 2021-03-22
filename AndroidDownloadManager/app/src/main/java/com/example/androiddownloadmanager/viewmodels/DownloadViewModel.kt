package com.example.androiddownloadmanager.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.example.androiddownloadmanager.customViews.DownloadView
import com.example.androiddownloadmanager.database.DownloadDao
import com.example.androiddownloadmanager.database.DownloadInfo
import kotlinx.coroutines.*

class DownloadViewModel(
    private val data: DownloadDao,
    private val context: Context?,
    app: Application
) : AndroidViewModel(app) {

    val infos = data.getAllCrypto()

    fun insert(info: DownloadInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                data.insert(info)
            }
        }
    }

    fun update(info: DownloadInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Log.i("aaa","id = ${info.dId}")

                data.update(info)
            }
        }
    }

    fun delete(info: DownloadInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                data.delete(info)
            }
        }
    }

    // in this function we return the name of all download files as an String Array
    fun getNames(): Array<String> = infos.value?.let {
        val a = arrayListOf<String>()
        for (i in it)
            a.add(i.name)
        a.toTypedArray()
    } ?: arrayOf()




}