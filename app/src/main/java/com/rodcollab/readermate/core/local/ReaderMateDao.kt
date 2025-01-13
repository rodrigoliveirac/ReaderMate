package com.rodcollab.readermate.core.local

import androidx.room.Dao
import androidx.room.Query
import com.rodcollab.readermate.core.local.model.BookEntity

@Dao
interface ReaderMateDao {

    @Query("SELECT * FROM BookEntity")
    fun getAllBooks(): List<BookEntity>
}