package com.example.data.record.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.record.db.Item
import com.example.domain.model.Record
import java.io.File

@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val date: String,
    val audioLength: String,
    val mediaFilePath: String
)
    : Item<Record>
{
    override fun toDomainObject(): Record {
        return Record(
            id = id,
            title = title,
            date = date,
            audioLength = audioLength,
            mediaFile = File(mediaFilePath)
        )
    }

    companion object {
        fun fromDomain(model: Record): RecordEntity {
            return RecordEntity(
                title = model.title,
                date = model.date,
                audioLength = model.audioLength,
                mediaFilePath = model.mediaFile.absolutePath
            )
        }
    }
}
