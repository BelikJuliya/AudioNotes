package com.example.data.record.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.record.db.entity.RecordEntity

@Dao
interface RecordsDao {
    @Query("SELECT * FROM records")
    fun getAllRecords(): List<RecordEntity>

    @Insert
    fun insertRecord(record: RecordEntity)
}