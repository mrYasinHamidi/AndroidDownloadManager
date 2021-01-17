package com.example.androiddownloadmanager.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androiddownloadmanager.database.DownloadDao
import com.example.androiddownloadmanager.viewmodels.DownloadViewModel

class DownloadViewModelFactory(private val data: DownloadDao, private val app: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadViewModel::class.java))
            return DownloadViewModel(data, app) as T
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}
