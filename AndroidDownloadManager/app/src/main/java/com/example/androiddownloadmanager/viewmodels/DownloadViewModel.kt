package com.example.androiddownloadmanager.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
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
    // TODO: Implement the ViewModel
    private val _views = MutableLiveData<MutableList<DownloadView>>()
    val views: LiveData<MutableList<DownloadView>>
        get() = _views

    private val _newView = MutableLiveData<DownloadView>()
    val newView: LiveData<DownloadView>
        get() = _newView


    init {
        /*
        * in start of view model
        * we receive all data from database at once (DownloadInfo.kt)
        * then we set a DownloadView for each of them (DownloadView.kt)
        * and finally we create a list of DownloadViews as LiveData to observe it DownloadFragment.kt
        */
        viewModelScope.launch {
            val downloadIfs = runBlocking(Dispatchers.IO) {
                data.getAllCrypto()
            }
            val downloadViews = mutableListOf<DownloadView>()
            for (i in downloadIfs) {
                val view = DownloadView(context)
                view.setDownloadInformation(i)
                downloadViews.add(view)
            }
            _views.value = downloadViews
        }
    }

    fun addDownloadInfo(info: DownloadInfo) {
        //save this info to database with none state (with none state it start automatically)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                data.insert(info)
            }
            val view = DownloadView(context)
            view.setDownloadInformation(info)
            views.value?.add(view)
            _newView.postValue(view)
        }
    }

    fun removeDownloadInfo(info: DownloadInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                data.delete(info)
            }
        }
    }


    // in this function we return the name of all download files as an String Array
    fun getNames(): Array<String> = views.value?.let {
        val a = arrayListOf<String>()
        for (i in it)
            a.add(i.toString())
        a.toTypedArray()
    } ?: arrayOf()


}

