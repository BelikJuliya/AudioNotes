package com.example.data.record.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DbMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            recreateProfile(database)
        }
    }
    private fun recreateProfile(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS Record")

        database.execSQL(
            """CREATE TABLE IF NOT EXISTS `Record` (
                `id` TEXT PRIMARY KEY NOT NULL,
                `title` TEXT NOT NULL DEFAULT '', 
                `date` TEXT NOT NULL DEFAULT '', 
                `audioLength` TEXT NOT NULL DEFAULT '', 
                `mediaFilePath` TEXT NOT NULL DEFAULT ''
             )"""
        )
    }
}