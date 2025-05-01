package com.rodcollab.readermate.core.model

data class BookEntity(
    val uuid: String,
    val title: String,
    val author: String,
    val description: String,
    val totalPages: Int,
)