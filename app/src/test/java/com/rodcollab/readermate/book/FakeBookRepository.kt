package com.rodcollab.readermate.book

import com.rodcollab.readermate.core.model.BookEntity
import com.rodcollab.readermate.core.model.BookRecord
import com.rodcollab.readermate.core.model.Reading
import com.rodcollab.readermate.core.model.toBookEntity
import com.rodcollab.readermate.core.model.toBookRecord
import com.rodcollab.readermate.core.model.toReading
import com.rodcollab.readermate.core.model.toReadingEntity
import com.rodcollab.readermate.features.books.data.BookRepository
import com.rodcollab.readermate.mockdatasource.MockDataSource

class FakeBookRepository(private val dao: MockDataSource<BookEntity>) : BookRepository {

    /**
     * In the real repository, this method will use a dao.getCurrentReading() which is a query that returns
     * only the current reading based on `isCurrent` attribute.
     */
    override suspend fun addBook(book: BookRecord) {
        dao.createEntity(entity = book.toBookEntity())
    }

    override suspend fun getBookById(id: String): Result<BookRecord> = dao.getAll().find { it.uuid == id }?.toBookRecord()?.let {
        Result.success(it)
    } ?: throw Throwable("Could not find the book")
}