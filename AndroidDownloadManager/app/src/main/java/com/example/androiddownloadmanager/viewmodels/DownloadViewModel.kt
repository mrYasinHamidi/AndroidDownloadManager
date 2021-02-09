package com.example.androiddownloadmanager.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.androiddownloadmanager.database.DownloadDao
import com.example.androiddownloadmanager.database.DownloadInfo
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*

class DownloadViewModel(
    private val data: DownloadDao,
    private val context: Context?,
    app: Application
) : AndroidViewModel(app) {


    //create a Downloader
    private val fetchConfig = FetchConfiguration.Builder(getApplication())
        .setDownloadConcurrentLimit(3)
        .build()
    private val fetch = Fetch.Impl.getInstance(fetchConfig)

    //observable
    val downloadObservable: Observable<String> = Observable.create(ObservableOnSubscribe<String> {

        Log.i("Observable","Created")
        fetch.addListener( object : FetchListener {
            override fun onAdded(download: Download) {
                it.onNext("onAdded" + ',' + download.id )
            }

            override fun onCancelled(download: Download) {
            }

            override fun onCompleted(download: Download) {
                it.onNext("onCompleted" + ',' + download.id )
                it.onComplete()
            }

            override fun onDeleted(download: Download) {
            }

            override fun onDownloadBlockUpdated(
                download: Download,
                downloadBlock: DownloadBlock,
                totalBlocks: Int
            ) {
            }

            override fun onError(download: Download, error: Error, throwable: Throwable?) {
                it.onNext("onError" + ',' + download.id )
            }

            override fun onPaused(download: Download) {
                it.onNext("onPaused" + ',' + download.id )
            }

            override fun onProgress(
                download: Download,
                etaInMilliSeconds: Long,
                downloadedBytesPerSecond: Long
            ) {
                it.onNext("onProgress" + ',' + download.id + ',' + download.progress)
            }

            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
            }

            override fun onRemoved(download: Download) {
            }

            override fun onResumed(download: Download) {
                it.onNext("onResumed" + ',' + download.id )
            }

            override fun onStarted(
                download: Download,
                downloadBlocks: List<DownloadBlock>,
                totalBlocks: Int
            ) {
                it.onNext("onStarted" + ',' + download.id )
            }

            override fun onWaitingNetwork(download: Download) {
            }
        })
    })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    fun download(info: DownloadInfo) {
        val request = Request(info.url, info.path + '/' + info.name)
        info.dId = request.id
        update(info)
        fetch.enqueue(request)


    }

    fun pause(info: DownloadInfo) {
        info.dId?.let {
            fetch.pause(it)
        }
    }

    fun resume(info: DownloadInfo) {
        info.dId?.let {
            fetch.resume(it)
        }
    }


    //items in database
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
            a.add(i.toString())
        a.toTypedArray()
    } ?: arrayOf()

    fun reTry(info: DownloadInfo) {
        info.dId?.let {
            fetch.retry(it)
        }
    }


}

