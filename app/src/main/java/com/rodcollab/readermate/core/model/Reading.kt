package com.rodcollab.readermate.core.model

data class Reading(
    // TODO("the id should be a Long in the future, to avoid UUID ids")
    val uuid: String,
    val book: BookRecord,
    val goalPerDay: Int,
    val isCurrent: Boolean,
    val startedDate: String,
    val estimatedEndDate: String,
    val isCompleted: Boolean,
    val currentPage: Int,
)