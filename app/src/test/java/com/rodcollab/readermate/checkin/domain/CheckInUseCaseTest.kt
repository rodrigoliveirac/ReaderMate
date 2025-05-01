package com.rodcollab.readermate.checkin.domain

import com.rodcollab.readermate.book.FakeBookRepository
import com.rodcollab.readermate.checkin.data.FakeCheckInRepository
import com.rodcollab.readermate.core.model.BookEntity
import com.rodcollab.readermate.core.model.BookRecord
import com.rodcollab.readermate.core.model.CheckIn
import com.rodcollab.readermate.core.model.CheckInEntity
import com.rodcollab.readermate.core.model.Reading
import com.rodcollab.readermate.core.model.ReadingEntity
import com.rodcollab.readermate.features.books.data.BookRepository
import com.rodcollab.readermate.features.checkin.data.CheckInRepository
import com.rodcollab.readermate.features.checkin.domain.CheckInUseCase
import com.rodcollab.readermate.features.checkin.domain.CheckInUseCaseImpl
import com.rodcollab.readermate.features.reading.data.ReadingRepository
import com.rodcollab.readermate.mockdatasource.MockDataSource
import com.rodcollab.readermate.reading.FakeReadingRepository
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.properties.Delegates

class CheckInUseCaseTest {

    private lateinit var checkInDataSource: MockDataSource<CheckInEntity>
    private lateinit var checkInRepository: CheckInRepository
    private lateinit var readingDataSource: MockDataSource<ReadingEntity>
    private lateinit var readingRepository: ReadingRepository
    private lateinit var bookDataSource: MockDataSource<BookEntity>
    private lateinit var bookRepository: BookRepository
    private lateinit var checkInUseCase: CheckInUseCase
    private var goalPerDay by Delegates.notNull<Int>()

    @Before
    fun setup() = runTest {
        checkInDataSource = MockDataSource()
        readingDataSource = MockDataSource()
        bookDataSource = MockDataSource()

        checkInRepository = FakeCheckInRepository(dao = checkInDataSource)
        readingRepository = FakeReadingRepository(dao = readingDataSource)
        bookRepository = FakeBookRepository(dao = bookDataSource)

        checkInUseCase = CheckInUseCaseImpl(bookRepository, checkInRepository, readingRepository)
    }

    @Test
    fun `When the user achieve the goal, the goalAchieved should be true`() = runTest {
        // Given
        val alreadyReadTodayTotalPages = GOAL_PER_DAY + 1
        setupCheckInsMock(hasCheckInToday = true, isGoalAchieved = false, currentPage = alreadyReadTodayTotalPages)

        // When
        val oldCheckIn = checkInRepository.getTodayCheckIn()
        val result = checkInUseCase(currentPage = 11)

        // Then
        val expectedTotalPages = 10
        val expectedGoalAchieved = !oldCheckIn?.goalAchieved!!
        val checkInUpdated = checkInRepository.getTodayCheckIn()

        TestCase.assertEquals(result.getOrNull()?.goalAchieved, checkInUpdated?.goalAchieved!!)
        TestCase.assertEquals(result.getOrNull()?.totalPages, checkInUpdated.totalPages)
        TestCase.assertEquals(expectedGoalAchieved, checkInUpdated.goalAchieved)
        TestCase.assertEquals(expectedTotalPages, checkInUpdated.totalPages)
    }

    @Test
    fun `When the user do the check-in again, the totalPages should be counted properly`() = runTest {
        // Given
        setupCheckInsMock(hasCheckInToday = true, isGoalAchieved = true)

        // When
        val newCurrentPage = 15
        val oldCheckIn = checkInRepository.getTodayCheckIn()!!
        val result = checkInUseCase(currentPage = newCurrentPage)

        // Then
        val expectedTotalPages = newCurrentPage - 1
        val expectedGoalAchieved = oldCheckIn.goalAchieved
        val checkInUpdated = checkInRepository.getTodayCheckIn()!!

        TestCase.assertEquals(expectedTotalPages, checkInUpdated.totalPages)
        TestCase.assertEquals(expectedGoalAchieved, checkInUpdated.goalAchieved)
        TestCase.assertEquals(result.getOrNull()?.goalAchieved, checkInUpdated.goalAchieved)
        TestCase.assertEquals(result.getOrNull()?.totalPages, checkInUpdated.totalPages)
    }

    @Test
    fun `When the user do the check-in again without achieve the goal, the goalAchieved should be false`() = runTest {
        // Given
        setupCheckInsMock(hasCheckInToday = true, isGoalAchieved = false)

        // When
        val oldCheckIn = checkInRepository.getTodayCheckIn()!!
        val currentPage = 10
        val result = checkInUseCase(currentPage = currentPage)

        // Then
        val expectedTotalPages = oldCheckIn.totalPages.plus(currentPage - ONE_PAGE)
        val expectedGoal = oldCheckIn.goalAchieved
        val checkInUpdated = checkInRepository.getTodayCheckIn()!!

        TestCase.assertEquals(expectedGoal, checkInUpdated.goalAchieved)
        TestCase.assertEquals(expectedTotalPages, checkInUpdated.totalPages)
        TestCase.assertEquals(result.getOrNull()?.goalAchieved, checkInUpdated.goalAchieved)
        TestCase.assertEquals(result.getOrNull()?.totalPages, checkInUpdated.totalPages)
    }

    @Test
    fun `When the user do today's check-in for the first time achieving the goal, the goalAchieved should be true`() = runTest {
        // Given
        val bookTotalPages = 100
        setupCheckInsMock(hasCheckInToday = false, bookTotalPages = bookTotalPages)

        // When
        val expectedTotalPagesRead = bookTotalPages
        val result = checkInUseCase(currentPage = 0, isCompleted = true)

        // Then
        val checkInUpdated = checkInRepository.getTodayCheckIn()!!

        TestCase.assertEquals(true, checkInUpdated.goalAchieved)
        TestCase.assertEquals(expectedTotalPagesRead, checkInUpdated.totalPages)
        TestCase.assertEquals(result.getOrNull()?.goalAchieved, checkInUpdated.goalAchieved)
        TestCase.assertEquals(result.getOrNull()?.totalPages, checkInUpdated.totalPages)
    }

    @Test
    fun `When the user do today's check-in for the first time without achieving the goal, the goalAchieved should be false`() = runTest {
        // Given
        setupCheckInsMock(hasCheckInToday = false)

        // When
        val result = checkInUseCase(currentPage = 10)

        // Then
        val checkInUpdated = checkInRepository.getTodayCheckIn()!!

        TestCase.assertEquals(false, checkInUpdated.goalAchieved)
        TestCase.assertEquals(9, checkInUpdated.totalPages)
        TestCase.assertEquals(result.getOrNull()?.goalAchieved, checkInUpdated.goalAchieved)
        TestCase.assertEquals(result.getOrNull()?.totalPages, checkInUpdated.totalPages)
    }

    @Test
    fun `When the user do today's check-in without have a current reading, the CheckInUseCase should return OnFailure`() = runTest {
        // Given
        setupCheckInsMock(hasCheckInToday = false, withCurrentReading = false)

        // When
        val result = checkInUseCase(currentPage = TOTAL_PAGES_READ_TODAY)

        when {
            result.isSuccess -> TestCase.assertEquals(TOTAL_PAGES_READ_TODAY, result.getOrNull()?.totalPages)
            else -> TestCase.assertNull(result.getOrNull())
        }
    }

    @Test
    fun `When the user do today's check-in again, calculate the numberOfSessionsLeft properly`() = runTest {
        // Given
        setupCheckInsMock(
            bookTotalPages = 360,
            hasCheckInToday = true,
            isGoalAchieved = false,
            currentPage = 1
        )
        val currentPage = 10

        // When
        val result = checkInUseCase(currentPage = currentPage)

        // Then
        val expectedNumberOfSessionsLeft = 35
        val expectedTotalPagesRead = 9
        val currentReading = readingRepository.getCurrentReading()

        TestCase.assertEquals(currentPage, currentReading?.currentPage)
        TestCase.assertEquals(expectedNumberOfSessionsLeft, currentReading?.numberOfSessionsLeft)
        TestCase.assertEquals(expectedTotalPagesRead,result.getOrNull()?.totalPages)
    }

    @Test
    fun `When the user do today's check-in again and complete the book`() = runTest {
        // Given
        setupCheckInsMock(hasCheckInToday = true)

        // When
        val result = checkInUseCase(isCompleted = true)

        // Then
        val checkInUpdated = checkInRepository.getTodayCheckIn()!!

        TestCase.assertEquals(true, checkInUpdated.goalAchieved)
        TestCase.assertEquals(100, checkInUpdated.totalPages)
        TestCase.assertEquals(result.getOrNull()?.goalAchieved, checkInUpdated.goalAchieved)
        TestCase.assertEquals(result.getOrNull()?.totalPages, checkInUpdated.totalPages)
    }


    private suspend fun setupCheckInsMock(
        bookTotalPages: Int = TOTAL_PAGES_READ_TODAY,
        hasCheckInToday: Boolean = false,
        isGoalAchieved: Boolean = false,
        withCurrentReading: Boolean = true,
        currentPage: Int = 1,
    ) {
        var count = 1
        var currentReadingUuid: String? = null
        val book = BookRecord(
            uuid = "mock1",
            totalPages = bookTotalPages
        )
        bookRepository.addBook(book)
        if(withCurrentReading) {
            currentReadingUuid = UUID.randomUUID().toString()
            goalPerDay = GOAL_PER_DAY
            val newReading = Reading(
                uuid = currentReadingUuid,
                bookRecordId = "mock1",
                goalPerDay = GOAL_PER_DAY,
                isCurrent = true,
                startedDate = System.currentTimeMillis(),
                numberOfSessionsLeft = 0,
                currentPage = if(isGoalAchieved) goalPerDay + 1 else currentPage,
                isCompleted = false
            )
            readingRepository.addReading(reading = newReading)
        }

        while(count != 7) {
            val calendar = java.util.Calendar.getInstance()
            val day = calendar[java.util.Calendar.DAY_OF_WEEK]
            if(day == count) {
                if(hasCheckInToday) {
                    val checkIn = CheckIn(
                        uuid = UUID.randomUUID().toString(),
                        readingID = currentReadingUuid ?: "readingID-01",
                        totalPages = (if(isGoalAchieved) goalPerDay else currentPage - 1)!!,
                        createdAt = System.currentTimeMillis(),
                        goalAchieved = isGoalAchieved
                    )
                    checkInRepository.createCheckIn(checkIn)
                }
                break
            } else {
                val checkIn = CheckIn(
                    uuid = UUID.randomUUID().toString(),
                    readingID = currentReadingUuid ?: "readingID-01",
                    totalPages = 15,
                    createdAt = calendar.apply {
                        firstDayOfWeek = java.util.Calendar.SUNDAY
                        set(java.util.Calendar.DAY_OF_WEEK, count)
                    }.time.time,
                    goalAchieved = true
                )
                checkInRepository.createCheckIn(checkIn)
            }
            count += 1
        }
    }

    companion object {
        private const val ONE_PAGE = 1
        private const val TOTAL_PAGES_READ_TODAY = 100
        private const val GOAL_PER_DAY = 10
    }

}