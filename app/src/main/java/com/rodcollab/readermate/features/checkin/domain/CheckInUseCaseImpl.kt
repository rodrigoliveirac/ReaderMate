package com.rodcollab.readermate.features.checkin.domain

import com.rodcollab.readermate.core.model.CheckIn
import com.rodcollab.readermate.core.model.Reading
import com.rodcollab.readermate.features.books.data.BookRepository
import com.rodcollab.readermate.features.checkin.data.CheckInRepository
import com.rodcollab.readermate.features.reading.data.ReadingRepository
import kotlin.math.ceil

/*
 * Use this function to define the button of check-in in the home screen
 * */
internal fun isNearToFinish(
    goalPerDay: Int,
    lastCurrentPageNumber: Int,
    totalBookPages: Int,
): Boolean {
    val lastPage = 1
    return goalPerDay.plus(lastCurrentPageNumber + lastPage) >= totalBookPages
}

sealed interface CheckInButton {
    data object FinishReading : CheckInButton

    data object CheckInReading : CheckInButton
}

class CheckInUseCaseImpl(
    private val bookRepository: BookRepository,
    private val checkInRepository: CheckInRepository,
    private val readingRepository: ReadingRepository,
) : CheckInUseCase {

    override suspend fun invoke(currentPage: Int, isCompleted: Boolean): Result<CheckIn> =
        runCatching {
            readingRepository.getCurrentReading()?.let { currentReading ->
                val currentBook = getCurrentBook(currentReading)
                val bookTotalPages = currentBook.totalPages
                newCheckIn(
                        currentReading = currentReading,
                        totalPagesRead =
                            if (isCompleted) bookTotalPages
                            else currentPage.minus(currentReading.currentPage),
                    )
                    .also {
                        val newCurrentReading =
                            currentReading.copy(
                                currentPage = if (isCompleted) bookTotalPages else currentPage,
                                isCompleted = isCompleted,
                                isCurrent = !isCompleted,
                                numberOfSessionsLeft =
                                    if (isCompleted) NO_MORE_SESSIONS_LEFT
                                    else
                                        currentReading.calculateNumberOfSessionsLeft(
                                            bookTotalPages,
                                            currentPage,
                                        ),
                            )
                        readingRepository.updateReading(newCurrentReading)
                    }
            } ?: throw Throwable(message = "Current Reading does not exist")
        }

    private suspend fun getCurrentBook(currentReading: Reading) =
        (bookRepository.getBookById(currentReading.bookRecordId).getOrNull()
            ?: throw Throwable("Could not find the book selected for this reading"))

    private suspend fun newCheckIn(totalPagesRead: Int, currentReading: Reading): CheckIn =
        getAndUpdateCheckIn(currentReading, totalPagesRead)
            ?: getAndCreateCheckIn(currentReading, totalPagesRead)

    private suspend fun getAndUpdateCheckIn(
        currentReading: Reading,
        totalPagesRead: Int,
    ): CheckIn? =
        checkInRepository
            .getTodayCheckIn()
            ?.let { currentCheckIn ->
                currentCheckIn.copy(
                    totalPages = currentCheckIn.totalPages + totalPagesRead,
                    goalAchieved =
                        currentReading.goalPerDay <= currentCheckIn.totalPages + totalPagesRead,
                )
            }
            ?.also { checkInUpdated -> checkInRepository.updateCheckIn(checkInUpdated) }

    private suspend fun getAndCreateCheckIn(currentReading: Reading, totalPages: Int): CheckIn =
        CheckIn(
                readingID = currentReading.uuid,
                totalPages = totalPages,
                createdAt = System.currentTimeMillis(),
                goalAchieved = currentReading.goalPerDay <= totalPages,
            )
            .also { checkInCreated -> checkInRepository.createCheckIn(checkInCreated) }

    private fun Reading.calculateNumberOfSessionsLeft(totalPages: Int, currentPage: Int): Int {
        val pagesLeftToRead = totalPages - currentPage + INCLUDE_THE_LAST_CURRENT_PAGE
        return (pagesLeftToRead.toDouble() / goalPerDay).toInt()
    }

    companion object {
        private const val NO_MORE_SESSIONS_LEFT = 0
        private const val INCLUDE_THE_LAST_CURRENT_PAGE = 1
    }
}
