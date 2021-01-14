package com.example.androiddownloadmanager

data class DownloadInfo(
    val name: String,
    val url: String,
    val path: String,
    val size: Long,
    var state: DownloadState
)