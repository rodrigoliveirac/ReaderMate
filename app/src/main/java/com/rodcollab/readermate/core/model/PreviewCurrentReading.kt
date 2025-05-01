package com.rodcollab.readermate.core.model

import com.rodcollab.readermate.core.common.DateUtils.toDateText
import java.time.LocalDate


//data class CurrentReading(
//    val uuid: String,
//    val bookRecordId: String,
//    val goalPerDay: Int,
//    val isCurrent: Boolean,
//    val startedDate: Long,
//    val pagesRead: Int,
//    val bookTotalPages: Int,
//
//    ) {
//    val estimatedEndNumberOfSessions: Int = getSessionsLeft()
//    val estimatedEndDate: String = getEstimatedEndInDate()
//
//    private fun getSessionsLeft(): Int = (bookTotalPages - pagesRead) / goalPerDay
//
//    private fun getEstimatedEndInDate(): String =
//        LocalDate.now().plusDays(getSessionsLeft().toLong()).toDateText()
//}

class CurrentReading(
    val uuid: String?,
    val bookRecordId: String?,
    val goalPerDay: Int?,
    val isCurrent: Boolean?,
    val startedDate: Long?,
    val estimatedEndInNumberOfSessions: Int,
    val isCompleted: Boolean?,
    val currentPage: Int?,
) {

    private constructor(builder: Builder) : this(
        builder.uuid,
        builder.bookRecordId,
        builder.goalPerDay,
        builder.isCurrent,
        builder.startedDate,
        builder.estimatedEndInNumberOfSessions,
        builder.isCompleted,
        builder.currentPage,

        )

    class Builder {
        var uuid: String? = null
            private set

        var bookRecordId: String? = null
            private set

        var goalPerDay: Int? = null
            private set

        var isCurrent: Boolean? = null
            private set

        var startedDate: Long? = null
            private set

        var estimatedEndInNumberOfSessions: Int = 0
            private set

        var isCompleted: Boolean? = null
            private set

        var currentPage: Int? = null
            private set

        fun sessionsLeft(totalPages: Int, pagesRead: Int) = apply {
            this.estimatedEndInNumberOfSessions = (totalPages - pagesRead) / (goalPerDay ?: throw IllegalArgumentException("The goalPerDay should be set"))
            if (currentPage == totalPages) {
                this.isCompleted = true
            } else {
                this.isCurrent = false
            }
        }

        fun goalPerDay(goalPerDay: Int) = apply { this.goalPerDay = goalPerDay }

        fun currentPage(currentPage: Int) = apply { this.currentPage = currentPage }

        fun uuid(uuid: String) = apply { this.uuid = uuid }

        fun bookRecordId(bookRecordId: String) = apply { this.bookRecordId = bookRecordId }

        fun build() = Reading(
            uuid ?: throw IllegalArgumentException("The uuid should be set"),
            bookRecordId ?: throw IllegalArgumentException("The bookRecordId should be set"),
            goalPerDay ?: throw IllegalArgumentException("The goalPerDay should be set"),
            isCurrent ?: throw IllegalArgumentException("The isCurrent should be set"),
            startedDate ?: throw IllegalArgumentException("The startedDate should be set"),
            estimatedEndInNumberOfSessions,
            isCompleted ?: throw IllegalArgumentException("The isCompleted should be set"),
            currentPage?: throw IllegalArgumentException("The currentPage should be set")
        )
    }
}