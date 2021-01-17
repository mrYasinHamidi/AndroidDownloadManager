package com.example.androiddownloadmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DownloadInfo::class], version = 2, exportSchema = false)
abstract class DownloadDatabase : RoomDatabase() {
    abstract val dao:DownloadDao
}
private lateinit var INSTANCE: DownloadDatabase

fun getDatabase(context: Context): DownloadDatabase {
    synchronized(DownloadDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                DownloadDatabase::class.java,
                "DownloadInfo"
            ).build()
        }
    }
    return INSTANCE
}