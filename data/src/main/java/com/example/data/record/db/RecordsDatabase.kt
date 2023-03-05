package com.example.data.record.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.record.db.entity.RecordEntity


@Database(entities = [RecordEntity::class], version = 1)
abstract class RecordsDatabase : RoomDatabase() {
    abstract fun recordsDao(): RecordsDao

}