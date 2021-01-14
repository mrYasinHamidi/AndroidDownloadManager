package com.example.androiddownloadmanager.request

import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddownloadmanager.utility.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.random.Random

class RequestViewModel : ViewModel() {
    //a variable for returning name of download file
    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    //a variable for returning size of download file
    private val _size = MutableLiveData<Long>()
    val size: LiveData<Long>
        get() = _size

    //a variable for returning errors
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    //a variable for returning directory that downloaded file will be saved in
    private val _path =
        MutableLiveData<String>(Environment.getExternalStorageDirectory().absolutePath + "/" + "AndroidDownloadManager")
    val path: LiveData<String>
        get() = _path

    //it is return the state of request (eg: Loading , Successful)
    private val _status = MutableLiveData<RequestState>(RequestState.NONE)
    val status: LiveData<RequestState>
        get() = _status


    //a storage permission for start downloading ; if it is true we have enough storage space , if it is false we haven't enough storage space
    private val _areSpaceAvailable = MutableLiveData<Boolean>()
    val areSpaceAvailable: LiveData<Boolean>
        get() = _areSpaceAvailable

    fun getStatus(url: String) {
        /*get a download link
        * check connection for this link
        * get size of content that is in link
        * handel errors
        */
        viewModelScope.launch {
            _status.postValue(RequestState.LOADING)
            try {
                startConnection(url)
                _status.postValue(RequestState.SUCCESSFUL)
            } catch (e: Exception) {
                _error.postValue(e.message)
                _status.postValue(RequestState.ERROR)
            }
        }
    }

    private suspend fun startConnection(url: String) {
        withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection()
                connection.connectTimeout = 1000
                connection.connect()
                _name.postValue(url.split("/").last())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    _size.postValue(connection.contentLengthLong)
                } else
                    _size.postValue(connection.contentLength.toLong())
            } catch (e: Exception) {
                throw Exception(e.message ?: "Wooooow !!! Something want wrong , please try again")
            }
        }
    }

    fun clareStatus() {
        _status.value = RequestState.NONE
    }


}

