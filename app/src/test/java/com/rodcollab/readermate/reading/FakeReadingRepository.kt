package com.rodcollab.readermate.reading

import com.rodcollab.readermate.core.model.Reading
import com.rodcollab.readermate.core.model.ReadingEntity
import com.rodcollab.readermate.core.model.toReading
import com.rodcollab.readermate.core.model.toReadingEntity
import com.rodcollab.readermate.features.reading.data.ReadingRepository
import com.rodcollab.readermate.mockdatasource.MockDataSource
import java.util.UUID

class FakeReadingRepository(private val dao: MockDataSource<ReadingEntity>) : ReadingRepository {


    init {
        setupCompletedReadingsMock()
    }

    private fun setupCompletedReadingsMock() {
        val newReading1 = Reading(
            bookRecordId = "mock1",
            goalPerDay = 10,
            isCurrent = false,
            startedDate = System.currentTimeMillis(),
            estimatedEndDate = System.currentTimeMillis() + 1000L,
            currentPage = 0,
            isCompleted = true
        )
        val newReading2 = Reading(
            bookRecordId = "mock2",
            goalPerDay = 10,
            isCurrent = false,
            startedDate = System.currentTimeMillis(),
            estimatedEndDate = System.currentTimeMillis() + 1000L,
            currentPage = 400,
            isCompleted = true
        )

        dao.createEntity(newReading1.toReadingEntity())
        dao.createEntity(newReading2.toReadingEntity())
    }

    /**
     * In the real repository, this method will use a dao.getCurrentReading() which is a query that returns
     * only the current reading based on `isCurrent` attribute.
     */
    override suspend fun getCurrentReading(): Reading? = try {
        var currentReading = dao.getAll().find { it.isCurrent }?.toReading()
        if(currentReading == null) {
            val newReading = Reading(
                uuid = UUID.randomUUID().toString(),
                bookRecordId = "mock1",
                goalPerDay = 10,
                isCurrent = true,
                startedDate = System.currentTimeMillis(),
                estimatedEndDate = System.currentTimeMillis() + 1000L,
                currentPage = 0,
                isCompleted = false
            ).toReadingEntity()
            dao.createEntity(entity = newReading)
            newReading.toReading()
        } else {
            currentReading
        }
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