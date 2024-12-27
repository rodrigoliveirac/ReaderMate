package com.rodcollab.readermate.features.checkin.domain

import com.rodcollab.readermate.core.common.ResultOf
import com.rodcollab.readermate.core.model.CheckIn
import com.rodcollab.readermate.features.checkin.data.CheckInRepository
import com.rodcollab.readermate.features.reading.data.ReadingRepository

class CheckInUseCaseImpl(
    private val checkInRepository: CheckInRepository,
    private val readingRepository: ReadingRepository,
) : CheckInUseCase {
    override suspend fun invoke(totalPages: Int): ResultOf<CheckIn> {

        return try {

            if (checkInRepository.hasCheckInToday()) {

                val checkIn = checkInRepository.getTodayCheckIn().let { todayCheckIn ->
                    CheckIn(
                        uuid = todayCheckIn.uuid,
                        readingID = todayCheckIn.readingID,
                        totalPages = todayCheckIn.totalPages + totalPages,
                        createdAt = todayCheckIn.createdAt,
                        goalAchieved = readingRepository.getCurrentReading()?.goalPerDay!! <= todayCheckIn.totalPages + totalPages
                    )
                }

                checkInRepository.updateCheckIn(checkIn)

                ResultOf.OnSuccess(checkIn)
            } else {

                val checkIn = CheckIn(
                    readingID = readingRepository.getCurrentReading()!!.uuid,
                    totalPages = totalPages,
                    createdAt = System.currentTimeMillis(),
                    goalAchieved = readingRepository.getCurrentReading()?.goalPerDay!! <= totalPages
                )

                checkInRepository.createCheckIn(checkIn)

                ResultOf.OnSuccess(checkIn)
            }
        } catch (e: Exception) {
            ResultOf.OnFailure(e.message)
        }
    }
}