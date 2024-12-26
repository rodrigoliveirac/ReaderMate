package com.rodcollab.readermate.features.reading.data

import com.rodcollab.readermate.core.model.Reading

interface ReadingRepository {
    suspend fun getCurrentReading() : Reading?
    suspend fun addReading(reading: Reading)
    suspend fun getReadingById(readingId: String) : Reading?
    suspend fun updateReading(reading: Reading)
    suspend fun deleteReading(readingId: String)
}