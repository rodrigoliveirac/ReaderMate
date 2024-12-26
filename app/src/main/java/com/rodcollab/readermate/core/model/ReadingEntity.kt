package com.rodcollab.readermate.core.model

data class ReadingEntity(
    // the id should be a Long in the future, to avoid UUID ids
    val uuid: String = "",
    val bookRecordId: String,
    val goalPerDay: Int,
    val isCurrent: Boolean,
    val startedDate: Long,
    val estimatedEndDate: Long,
    val isCompleted: Boolean,
    val currentPage: Int,
)