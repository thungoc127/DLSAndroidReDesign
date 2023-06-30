package com.example.dlsandroidredesign.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dlsandroidredesign.Converters

@Database(entities = [ImageLocationInfo::class], version = 1)
@TypeConverters(Converters::class)
abstract class DLSDatabase : RoomDatabase() {
    abstract val imageLocationInfoDAO: ImageLocationInfoDAO
}
