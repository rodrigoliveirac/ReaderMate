package com.rodcollab.readermate.core.local

import androidx.room.Dao
import androidx.room.Query
import com.rodcollab.readermate.core.local.model.BookRecordEntity

@Dao
interface ReaderMateDao {

    @Query("SELECT * FROM BookRecordEntity")
    fun getAllBooks(): List<BookRecordEntity>
}