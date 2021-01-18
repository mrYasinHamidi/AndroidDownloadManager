package com.example.androiddownloadmanager.app

import android.app.Application
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .setReadTimeout(30_000)
            .setConnectTimeout(30_000)
            .build()
        PRDownloader.initialize(applicationContext,config)
    }
}