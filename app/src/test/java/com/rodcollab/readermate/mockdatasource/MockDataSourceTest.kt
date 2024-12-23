package com.rodcollab.readermate.mockdatasource

import com.rodcollab.readermate.core.model.ReadingEntity
import org.junit.After
import org.junit.Test
import junit.framework.TestCase
import org.junit.Before

class MockDataSourceTest {

    private lateinit var readingSource: MockDataSource<ReadingEntity>

    @Before
    fun setup() {
        readingSource = MockDataSource()
        val newReading1 = ReadingEntity(
            bookRecordId = "mock1",
            goalPerDay = 10,
            isCurrent = false,
            startedDate = "00/00/00",
            estimatedEndDate = "00/00/00",
            currentPage = 0,
            isCompleted = true
        )
        val newReading2 = ReadingEntity(
            bookRecordId = "mock2",
            goalPerDay = 20,
            isCurrent = true,
            startedDate = "00/00/00",
            estimatedEndDate = "00/00/00",
            currentPage = 0,
            isCompleted = false
        )
        readingSource.createEntity(newReading1)
        readingSource.createEntity(newReading2)
    }

    @After
    fun clearDataSource() {
        readingSource.removeAll()
    }

    @Test
    fun `when the client create a item, the data source should have the new item`() {
        // when
        val newReading = ReadingEntity(
            bookRecordId = "something2",
            goalPerDay = 10,
            isCurrent = true,
            startedDate = "00/00/00",
            estimatedEndDate = "00/00/00",
            currentPage = 0,
            isCompleted = true
        )

        // given
        val oldSize = readingSource.getAll().size
        readingSource.createEntity(newReading)

        // then
        val newList = readingSource.getAll()
        val newSize = newList.size

        TestCase.assertEquals(oldSize + 1, newSize)
    }

    @Test
    fun `when the client get a item, the data source should return the respective item`() {
        // when
        val newReading1 = ReadingEntity(
            bookRecordId = "something2",
            goalPerDay = 10,
            isCurrent = false,
            startedDate = "00/00/00",
            estimatedEndDate = "00/00/00",
            currentPage = 0,
            isCompleted = true
        )
        val newReading2 = ReadingEntity(
            bookRecordId = "something3",
            goalPerDay = 20,
            isCurrent = true,
            startedDate = "00/00/00",
            estimatedEndDate = "00/00/00",
            currentPage = 0,
            isCompleted = false
        )
        readingSource.createEntity(newReading1)
        readingSource.createEntity(newReading2)

        // given
        val selectedItem = readingSource.getAll().random()

        // then
        val getReading = readingSource.readyById(selectedItem.uuid)


        TestCase.assertEquals(selectedItem, getReading)
    }

    @Test
    fun `when the client get a item and change the currentPage, the data source should update the list`() {
        // when
        val reading = readingSource.getAll()
        val selectReading = reading.random()

        // given
        val newReading = selectReading.copy(currentPage = selectReading.currentPage + 10)
        readingSource.update(newReading)

        // then
        val oldReading = selectReading
        val readingUpdated = readingSource.readyById(selectReading.uuid)


        TestCase.assertNotSame(oldReading.currentPage, readingUpdated?.currentPage)
    }

    @Test
    fun `when the client remove a reading, the data source should update the list without the reading`() {
        // when
        val reading = readingSource.getAll()

        // given
        val selectReading = reading.random()
        readingSource.delete(selectReading.uuid)

        // then
        val getReadingDeleted = readingSource.readyById(selectReading.uuid)

        TestCase.assertNull(getReadingDeleted)
    }
}