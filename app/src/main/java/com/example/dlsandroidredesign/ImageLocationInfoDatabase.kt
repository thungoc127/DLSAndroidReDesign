package com.example.dlsandroidredesign

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [ImageLocationInfo::class], version = 1)
@TypeConverters(Converters::class)
abstract class ImageLocationInfoDatabase: RoomDatabase() {
    abstract val imageLocaitonInfoDAO:ImageLocaitonInfoDAO
}