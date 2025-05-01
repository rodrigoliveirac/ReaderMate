package com.rodcollab.readermate.core.model

data class CheckIn(
    val uuid: String = "",
    val readingID: String,
    val totalPages: Int = 0,
    val createdAt: Long,
    val goalAchieved: Boolean
)