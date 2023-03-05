package com.example.domain.db

import com.example.domain.model.Record

interface DbRepository {
    suspend fun saveRecord(record: Record)
    suspend fun loadRecordsFromDb(): List<Record>
}