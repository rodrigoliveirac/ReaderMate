package com.rodcollab.readermate.reading

import com.rodcollab.readermate.core.model.Reading
import com.rodcollab.readermate.core.model.ReadingEntity
import com.rodcollab.readermate.core.model.toReading
import com.rodcollab.readermate.core.model.toReadingEntity
import com.rodcollab.readermate.features.reading.data.ReadingRepository
import com.rodcollab.readermate.mockdatasource.MockDataSource

class FakeReadingRepository(private val dao: MockDataSource<ReadingEntity>) : ReadingRepository {

    /**
     * In the real repository, this method will use a dao.getCurrentReading() which is a query that returns
     * only the current reading based on `isCurrent` attribute.
     */
    override suspend fun getCurrentReading(): Reading? = try {
        dao.getAll().find { it.isCurrent }?.toReading()
    } catch (e: Exception) {
        null
    }


    override suspend fun addReading(reading: Reading) = try {
        dao.createEntity(entity = reading.toReadingEntity())
    } catch (e: Exception) {

    }

    override suspend fun getReadingById(readingId: String): Reading? = try {
        dao.readyById(readingId)?.toReading()
    } catch (e: Exception) {
        null
    }

    override suspend fun updateReading(reading: Reading) = try {
        dao.update(entity = reading.toReadingEntity())
    } catch (e: Exception) {

    }

    override suspend fun deleteReading(readingId: String) = try {
        dao.delete(readingId)
    } catch (e: Exception) {

    }
}