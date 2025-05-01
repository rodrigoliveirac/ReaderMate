package com.rodcollab.readermate.core.model

import com.rodcollab.readermate.core.common.DateUtils
import com.rodcollab.readermate.core.common.DateUtils.toDateText
import java.time.LocalDate

/**
 * @param numberOfSessionsLeft the number of sessions left to the user finish the current reading,
 * @param estimatedEndInDate the estimated date that the user probably will finished the reading
 */
data class Reading(
    // TODO("the id should be a Long in the future, to avoid UUID ids")
    val uuid: String = "",
    val bookRecordId: String,
    val goalPerDay: Int,
    val isCurrent: Boolean,
    val startedDate: Long,
    val numberOfSessionsLeft: Int,
    val isCompleted: Boolean,
    val currentPage: Int = 1,
) {
    val startedDateFormatted: String = DateUtils.getDateFormatted(startedDate)
    val estimatedEndInDate: String = numberOfSessionsLeft.toDateText()

    private fun Int.toDateText(): String {
        val localDate = LocalDate.now().plusDays(this.toLong())
        return localDate.toDateText()
    }
    companion object {
        fun create(
            uuid: String,
            bookRecordId: String,
            goalPerDay: Int,
            isCurrent: Boolean,
            startedDate: Long,
            totalPages: Int,
            pagesRead: Int,
            currentPage: Int
        ): Reading {
            val estimatedEndInNumberOfSessions = (totalPages - pagesRead) / goalPerDay
            val isCompleted = pagesRead >= totalPages
            return Reading(
                uuid = uuid,
                bookRecordId = bookRecordId,
                goalPerDay = goalPerDay,
                isCurrent = isCurrent,
                startedDate = startedDate,
                numberOfSessionsLeft = estimatedEndInNumberOfSessions,
                isCompleted = isCompleted,
                currentPage = currentPage
            )
        }
    }
}
