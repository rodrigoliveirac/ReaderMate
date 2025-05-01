package com.rodcollab.readermate.features.checkin.data

import com.rodcollab.readermate.core.model.CheckIn

interface CheckInRepository {
    suspend fun createCheckIn(checkIn: CheckIn)
    suspend fun getCheckIn(checkInId: String): CheckIn?
    suspend fun updateCheckIn(checkIn: CheckIn)
    suspend fun getCurrentWeekCheckIns(): List<CheckIn>
    suspend fun hasCheckInToday(): Boolean
    suspend fun getTodayCheckIn(): CheckIn?
    suspend fun removeAll()
}