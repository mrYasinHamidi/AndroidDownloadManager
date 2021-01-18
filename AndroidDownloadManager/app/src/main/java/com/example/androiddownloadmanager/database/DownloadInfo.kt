package com.example.androiddownloadmanager.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androiddownloadmanager.DownloadState

@Entity
data class DownloadInfo(
    @PrimaryKey
    val name: String,
    val url: String,
    val path: String,
    val size: Long,
    var state: Int,
    var dId: Int? = null
)