package com.rodcollab.readermate.core.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Books that the user will choose to read
 */
@Entity
data class BookRecordEntity(
    // TODO("the id should be a Long in the future, to avoid UUID ids")
    @PrimaryKey val uuid: String,
    @ColumnInfo("title") val title: String,
    @ColumnInfo("author") val author: String,
    @ColumnInfo("description") val description: String,
    @ColumnInfo("total_pages") val totalPages: Int,
)
