package com.example.androiddownloadmanager.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DownloadDao {
    @Query("SELECT * FROM DownloadInfo")
    fun getAllCrypto(): LiveData<List<DownloadInfo>>
    @Insert
    fun insert(info: DownloadInfo)
    @Delete
    fun delete(info: DownloadInfo)
    @Update
    fun update(info: DownloadInfo)
}