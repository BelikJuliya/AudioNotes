package com.example.data.record.record

import com.example.data.record.db.entity.RecordEntity
import com.example.data.record.db.RecordsDao
import com.example.domain.model.Record
import com.example.domain.db.DbRepository

class RecordsRepositoryImpl(
    private val recordsDao: RecordsDao
): DbRepository {

    override suspend fun saveRecord(record: Record) {
        recordsDao.insertRecord(RecordEntity.fromDomain(record))
    }

    override suspend fun loadRecordsFromDb(): List<Record> {
        return recordsDao.getAllRecords().map { it.toDomainObject() }
    }
}