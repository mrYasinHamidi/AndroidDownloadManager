package com.example.androiddownloadmanager.database

import android.content.Context
import androidx.room.*

@Database(entities = [DownloadInfo::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
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