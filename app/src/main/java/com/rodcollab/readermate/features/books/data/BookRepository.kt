package com.rodcollab.readermate.features.books.data

import com.rodcollab.readermate.core.model.BookRecord

interface BookRepository {
    suspend fun addBook(book: BookRecord)
    suspend fun getBookById(id: String) : Result<BookRecord>
}