package com.rodcollab.readermate.reading

import com.rodcollab.readermate.core.model.Reading
import com.rodcollab.readermate.core.model.ReadingEntity
import com.rodcollab.readermate.features.reading.data.ReadingRepository
import com.rodcollab.readermate.mockdatasource.MockDataSource
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ReadingRepositoryTest {

    private lateinit var dataSource: MockDataSource<ReadingEntity>
    private lateinit var readingRepository: ReadingRepository

    @Before
    fun setup() {
        dataSource = MockDataSource()
        readingRepository = FakeReadingRepository(dao = dataSource)
    }

    @Test
    fun `When a new reading is added, it should be set as the current reading`() = runTest {
        // Given
        val newReading = Reading(
            bookRecordId = "mock1",
            goalPerDay = 10,
            isCurrent = true,
            startedDate = System.currentTimeMillis(),
            estimatedEndDate = System.currentTimeMillis() + 1000L,
            currentPage = 0,
            isCompleted = false
        )

        // When
        readingRepository.addReading(reading = newReading)

        // Then
        val currentReading = readingRepository.getCurrentReading()

        TestCase.assertEquals(newReading.startedDate, currentReading?.startedDate)
    }

    @Test
    fun `When a reading is selected, the reading data should be get the uuid properly`() = runTest {
        // Given
        val readingList = dataSource.getAll()

        // When
        val readingSelected = readingList.random()

        // Then
        val actualReadingSelected = readingRepository.getReadingById(readingId = readingSelected.uuid)

        TestCase.assertEquals(readingSelected.uuid, actualReadingSelected?.uuid)
    }

    @Test
    fun `When the user finish a reading, isCurrent should be false and isCompleted should be true`() = runTest {
        // Given
        val currentReading = readingRepository.getCurrentReading()!!

        // When
        readingRepository.updateReading(currentReading.copy(isCurrent = false, isCompleted = true))

        // Then
        val currentReadingUuid = currentReading.uuid
        val readingUpdated = readingRepository.getReadingById(readingId = currentReadingUuid)

        TestCase.assertEquals(!currentReading.isCurrent, readingUpdated?.isCurrent)
        TestCase.assertEquals(!currentReading.isCompleted, readingUpdated?.isCompleted)
    }

    @Test
    fun `When the user delete a reading, it should not exist in the data source anymore`() = runTest {
        // Given
        val currentReading = readingRepository.getCurrentReading()!!

        // When
        readingRepository.deleteReading(currentReading.uuid)

        // Then
        val currentReadingUuid = currentReading.uuid
        val readingRemoved = readingRepository.getReadingById(readingId = currentReadingUuid)

        TestCase.assertNull(readingRemoved)
    }
}