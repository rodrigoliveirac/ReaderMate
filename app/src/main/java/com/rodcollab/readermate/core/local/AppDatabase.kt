package com.rodcollab.readermate.core.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rodcollab.readermate.core.local.model.BookRecordEntity

@Database(entities = [BookRecordEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun readerMateDao(): ReaderMateDao
}