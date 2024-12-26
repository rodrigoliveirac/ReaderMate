package com.rodcollab.readermate.core.model

import com.rodcollab.readermate.core.common.DateUtils

data class Reading(
    // TODO("the id should be a Long in the future, to avoid UUID ids")
    val uuid: String = "",
    val bookRecordId: String,
    val goalPerDay: Int,
    val isCurrent: Boolean,
    val startedDate: Long,
    val estimatedEndDate: Long,
    val isCompleted: Boolean,
    val currentPage: Int,
) {
    val startedDateFormatted: String = DateUtils.getDateFormatted(startedDate)
    val estimatedEndDateFormatted: String = DateUtils.getDateFormatted(estimatedEndDate)
}