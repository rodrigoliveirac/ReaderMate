package com.rodcollab.readermate.core.model

import com.rodcollab.readermate.core.common.DateUtils.toDateText
import java.time.LocalDate

data class ReadingEntity(
    // the id should be a Long in the future, to avoid UUID ids
    val uuid: String = "",
    val bookRecordId: String,
    val goalPerDay: Int,
    val isCurrent: Boolean,
    val startedDate: Long,
    val estimatedEndInNumberOfSessions: Int,
    val isCompleted: Boolean,
    val currentPage: Int,
)