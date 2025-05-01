package com.rodcollab.readermate.core.model

/**
 * Books that the user will choose to read
 */
data class BookRecord(
    // TODO("the id should be a Long in the future, to avoid UUID ids")
    val uuid: String = "",
    val title: String = "",
    val author: String = "",
    val description: String = "",
    val totalPages: Int = 0,
)
