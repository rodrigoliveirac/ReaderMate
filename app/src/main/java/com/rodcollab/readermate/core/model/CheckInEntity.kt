package com.rodcollab.readermate.core.model

data class CheckInEntity(
    val uuid: String,
    val readingID: String,
    val totalPages: Int,
    val createdAt: Long,
    val goalAchieved: Boolean
)