package com.example.domain.usecase

import com.example.domain.model.Record
import com.example.domain.db.DbRepository

class LoadRecordsUseCase(
    private val dbRepository: DbRepository

) {
    suspend fun saveRecord(record: Record){
        dbRepository.saveRecord(record)
    }

    suspend fun loadRecordsFromDb() = dbRepository.loadRecordsFromDb()

}