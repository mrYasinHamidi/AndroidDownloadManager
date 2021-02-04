package com.example.androiddownloadmanager.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.androiddownloadmanager.DownloadState

@Entity
data class DownloadInfo(
    @PrimaryKey
    val name: String,
    val url: String,
    val path: String,
    val size: Long,
    var state: DownloadState,
    var dId: Int? = null
)

class Converters {

    @TypeConverter
    fun toHealth(value: String) = enumValueOf<DownloadState>(value)

    @TypeConverter
    fun fromHealth(value: DownloadState) = value.name
}