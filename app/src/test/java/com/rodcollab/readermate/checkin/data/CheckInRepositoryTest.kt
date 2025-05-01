package com.rodcollab.readermate.checkin.data

import com.rodcollab.readermate.core.common.DateUtils.toLocalDate
import com.rodcollab.readermate.core.model.CheckIn
import com.rodcollab.readermate.core.model.CheckInEntity
import com.rodcollab.readermate.features.checkin.data.CheckInRepository
import com.rodcollab.readermate.mockdatasource.MockDataSource
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.util.UUID

class CheckInRepositoryTest {

    private lateinit var dataSource: MockDataSource<CheckInEntity>
    private lateinit var checkInRepository: CheckInRepository

    @Before
    fun setup() {
        dataSource = MockDataSource()
        checkInRepository = FakeCheckInRepository(dao = dataSource)
    }

    @After
    fun restore() = runTest {
        checkInRepository.removeAll()
    }

    @Test
    fun `When the user do the check-in, it should be add on the data source`() = runTest {
        // given
        setupCheckInsMock()

        // when
        val hasCheckInToday = checkInRepository.hasCheckInToday()
        val checkIn = CheckIn(
            uuid = UUID.randomUUID().toString(),
            readingID = "readingID-01",
            totalPages = 15,
            createdAt = System.currentTimeMillis(),
            goalAchieved = true
        )
        checkInRepository.createCheckIn(checkIn)

        // then
        val hasCheckInTodayUpdated = checkInRepository.hasCheckInToday()

        TestCase.assertFalse(hasCheckInToday)
        TestCase.assertTrue(hasCheckInTodayUpdated)
    }

    @Test
    fun `When the user want to update the today's check-in, the today's check-in should be updated`() = runTest {
        // given
        setupCheckInsMock(withTodayCheckIn = true)

        // when
        val todayCheckIn = checkInRepository.getTodayCheckIn()!!
        val checkInUpdated = todayCheckIn.copy(totalPages = todayCheckIn.totalPages + 5, goalAchieved = true)
        checkInRepository.updateCheckIn(checkInUpdated)

        // then
        val todayCheckInUpdated = checkInRepository.getTodayCheckIn()!!

        TestCase.assertEquals(true, todayCheckInUpdated.goalAchieved)
        TestCase.assertEquals(15, todayCheckInUpdated.totalPages)
    }

    @Test
    fun `Get the today's checkIn properly`() = runTest {
        // given
        setupCheckInsMock(withTodayCheckIn = true)

        // when
        val today = LocalDate.now()
        val todayCheckIn = checkInRepository.getTodayCheckIn()!!

        // then

        TestCase.assertEquals(today, todayCheckIn.createdAt.toLocalDate())
    }

    private suspend fun setupCheckInsMock(withTodayCheckIn: Boolean = false) {
        var count = 1
        while(count != 7) {
            val calendar = java.util.Calendar.getInstance()
            val day = calendar[java.util.Calendar.DAY_OF_WEEK]
            if(day == count) {
                if(withTodayCheckIn) {
                    val checkIn = CheckIn(
                        uuid = UUID.randomUUID().toString(),
                        readingID = "readingID-01",
                        totalPages = 10,
                        createdAt = System.currentTimeMillis(),
                        goalAchieved = false
                    )
                    checkInRepository.createCheckIn(checkIn)
                }
                break
            } else {
                val checkIn = CheckIn(
                    uuid = UUID.randomUUID().toString(),
                    readingID = "readingID-01",
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
}