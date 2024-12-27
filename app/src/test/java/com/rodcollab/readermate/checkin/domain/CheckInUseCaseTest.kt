package com.rodcollab.readermate.checkin.domain

import com.rodcollab.readermate.checkin.data.FakeCheckInRepository
import com.rodcollab.readermate.core.common.ResultOf
import com.rodcollab.readermate.core.model.CheckIn
import com.rodcollab.readermate.core.model.CheckInEntity
import com.rodcollab.readermate.core.model.Reading
import com.rodcollab.readermate.core.model.ReadingEntity
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
    private lateinit var checkInUseCase: CheckInUseCase
    private var goalPerDay by Delegates.notNull<Int>()

    @Before
    fun setup() = runTest {
        checkInDataSource = MockDataSource()
        readingDataSource = MockDataSource()

        checkInRepository = FakeCheckInRepository(dao = checkInDataSource)
        readingRepository = FakeReadingRepository(dao = readingDataSource)

        checkInUseCase = CheckInUseCaseImpl(checkInRepository,readingRepository)
    }

    @Test
    fun `When the user achieve the goal, the goalAchieved should be true`() = runTest {
        // Given
        setupCheckInsMock(withTodayCheckIn = true, withGoalAchieved = false)

        // When
        val oldCheckIn = checkInRepository.getTodayCheckIn()
        checkInUseCase(totalPages = goalPerDay)

        // Then
        val expectedTotalPages = oldCheckIn.totalPages + readingRepository.getCurrentReading()?.goalPerDay!!
        val expectedGoalAchieved = !oldCheckIn.goalAchieved
        val checkInUpdated = checkInRepository.getTodayCheckIn()

        TestCase.assertEquals(expectedGoalAchieved, checkInUpdated.goalAchieved)
        TestCase.assertEquals(expectedTotalPages, checkInUpdated.totalPages)
    }

    @Test
    fun `When the user do the check-in again, the totalPages should be counted properly`() = runTest {
        // Given
        setupCheckInsMock(withTodayCheckIn = true, withGoalAchieved = true)

        // When
        val oldCheckIn = checkInRepository.getTodayCheckIn()
        checkInUseCase(totalPages = goalPerDay)

        // Then
        val expectedTotalPages = oldCheckIn.totalPages.plus(goalPerDay)
        val expectedGoalAchieved = oldCheckIn.goalAchieved
        val checkInUpdated = checkInRepository.getTodayCheckIn()

        TestCase.assertEquals(expectedTotalPages, checkInUpdated.totalPages)
        TestCase.assertEquals(expectedGoalAchieved, checkInUpdated.goalAchieved)
    }

    @Test
    fun `When the user do the check-in again without achieve the goal, the goalAchieved should be false`() = runTest {
        // Given
        setupCheckInsMock(withTodayCheckIn = true, withGoalAchieved = false)

        // When
        val oldCheckIn = checkInRepository.getTodayCheckIn()
        checkInUseCase(totalPages = TOTAL_PAGES_READ_TODAY)

        // Then
        val expectedTotalPages = oldCheckIn.totalPages.plus(TOTAL_PAGES_READ_TODAY)
        val expectedGoal = oldCheckIn.goalAchieved
        val checkInUpdated = checkInRepository.getTodayCheckIn()

        TestCase.assertEquals(expectedGoal, checkInUpdated.goalAchieved)
        TestCase.assertEquals(expectedTotalPages, checkInUpdated.totalPages)
    }

    @Test
    fun `When the user do today's check-in for the first time achieving the goal, the goalAchieved should be true`() = runTest {
        // Given
        setupCheckInsMock(withTodayCheckIn = false)

        // When
        checkInUseCase(totalPages = goalPerDay)

        // Then
        val checkInUpdated = checkInRepository.getTodayCheckIn()

        TestCase.assertEquals(true, checkInUpdated.goalAchieved)
        TestCase.assertEquals(goalPerDay, checkInUpdated.totalPages)
    }

    @Test
    fun `When the user do today's check-in for the first time without achieving the goal, the goalAchieved should be false`() = runTest {
        // Given
        setupCheckInsMock(withTodayCheckIn = false)

        // When
        checkInUseCase(totalPages = TOTAL_PAGES_READ_TODAY)

        // Then
        val checkInUpdated = checkInRepository.getTodayCheckIn()

        TestCase.assertEquals(false, checkInUpdated.goalAchieved)
        TestCase.assertEquals(TOTAL_PAGES_READ_TODAY, checkInUpdated.totalPages)
    }

    @Test
    fun `When the user do today's check-in without have a current reading, the CheckInUseCase should return OnFailure`() = runTest {
        // Given
        setupCheckInsMock(withTodayCheckIn = false, withCurrentReading = false)

        // When
        val result = checkInUseCase(totalPages = TOTAL_PAGES_READ_TODAY)
        when(result) {
            is ResultOf.OnSuccess -> {
                // Then
                TestCase.assertEquals(TOTAL_PAGES_READ_TODAY, result.value.totalPages)
            }
            is ResultOf.OnFailure -> {
                // Then
                TestCase.assertNotNull(result.message, result)
            }
        }
    }


    private suspend fun setupCheckInsMock(withTodayCheckIn: Boolean = false, withGoalAchieved: Boolean = false, withCurrentReading: Boolean = true) {
        var count = 1
        var currentReadingUuid: String? = null
        if(withCurrentReading) {
            currentReadingUuid = UUID.randomUUID().toString()
            val newReading = Reading(
                uuid = currentReadingUuid,
                bookRecordId = "mock1",
                goalPerDay = GOAL_PER_DAY,
                isCurrent = true,
                startedDate = System.currentTimeMillis(),
                estimatedEndDate = System.currentTimeMillis() + 1000L,
                currentPage = 0,
                isCompleted = false
            )
            readingRepository.addReading(reading = newReading)

            goalPerDay = GOAL_PER_DAY
        }

        while(count != 7) {
            val calendar = java.util.Calendar.getInstance()
            val day = calendar[java.util.Calendar.DAY_OF_WEEK]
            if(day == count) {
                if(withTodayCheckIn) {
                    val checkIn = CheckIn(
                        uuid = UUID.randomUUID().toString(),
                        readingID = currentReadingUuid ?: "readingID-01",
                        totalPages = (if(withGoalAchieved) readingRepository.getCurrentReading()?.goalPerDay else TOTAL_PAGES_READ_TODAY)!!,
                        createdAt = System.currentTimeMillis(),
                        goalAchieved = withGoalAchieved
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
        private const val TOTAL_PAGES_READ_TODAY = 1
        private const val GOAL_PER_DAY = 10
    }

}