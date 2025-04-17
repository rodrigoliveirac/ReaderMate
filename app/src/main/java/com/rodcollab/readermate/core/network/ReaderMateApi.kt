package com.rodcollab.readermate.core.network

import com.rodcollab.readermate.core.network.model.BookDto

interface ReaderMateApi {
    suspend fun getAllBooks(): List<BookDto>
}