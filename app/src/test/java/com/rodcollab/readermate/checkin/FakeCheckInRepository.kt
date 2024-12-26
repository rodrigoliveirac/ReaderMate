package com.rodcollab.readermate.checkin

import com.rodcollab.readermate.core.common.DateUtils.toLocalDate
import com.rodcollab.readermate.core.model.CheckIn
import com.rodcollab.readermate.core.model.CheckInEntity
import com.rodcollab.readermate.core.model.toCheckIn
import com.rodcollab.readermate.core.model.toCheckInEntity
import com.rodcollab.readermate.features.checkin.data.CheckInRepository
import com.rodcollab.readermate.mockdatasource.MockDataSource
import java.time.LocalDate
import java.util.Calendar

class FakeCheckInRepository(private val dao: MockDataSource<CheckInEntity>) : CheckInRepository {


    override suspend fun createCheckIn(checkIn: CheckIn) {
        dao.createEntity(checkIn.toCheckInEntity())
    }

    override suspend fun getCheckIn(checkInId: String): CheckIn? = dao.readyById(id = checkInId)?.toCheckIn()

    override suspend fun updateCheckIn(checkIn: CheckIn) {
        dao.update(checkIn.toCheckInEntity())
    }

    override suspend fun getCurrentWeekCheckIns(): List<CheckIn> {
        val startOfWeek = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.SUNDAY
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        }.time.time

        val endOfWeek = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.SUNDAY
            set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        }.time.time

        return dao.getAll().filter {
            it.createdAt in startOfWeek..endOfWeek
        }.map {
            it.toCheckIn()
        }
    }

    override suspend fun hasCheckInToday(): Boolean {
        val today = LocalDate.now()
        return !dao.getAll().none {
            it.createdAt.toLocalDate() == today
        }
    }

    override suspend fun getTodayCheckIn(): CheckIn {
        val today = LocalDate.now()
        return dao.getAll().find {
            it.createdAt.toLocalDate() == today
        }?.toCheckIn()!!
    }

    override suspend fun removeAll() {
        dao.removeAll()
    }
}