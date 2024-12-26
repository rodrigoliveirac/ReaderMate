package com.rodcollab.readermate.core.model

fun ReadingEntity.toReading() = Reading(
    uuid = uuid,
    bookRecordId = bookRecordId,
    goalPerDay = goalPerDay,
    isCurrent = isCurrent,
    startedDate = startedDate,
    estimatedEndDate = estimatedEndDate,
    isCompleted = isCompleted,
    currentPage = currentPage
)

fun Reading.toReadingEntity() = ReadingEntity(
    uuid = uuid,
    bookRecordId = bookRecordId,
    goalPerDay = goalPerDay,
    isCurrent = isCurrent,
    startedDate = startedDate,
    estimatedEndDate = estimatedEndDate,
    isCompleted = isCompleted,
    currentPage = currentPage
)

fun CheckInEntity.toCheckIn() = CheckIn(
    uuid = uuid,
    readingID = readingID,
    totalPages = totalPages,
    createdAt = createdAt,
    goalAchieved = goalAchieved
)

fun CheckIn.toCheckInEntity() = CheckInEntity(
    uuid = uuid,
    readingID = readingID,
    totalPages = totalPages,
    createdAt = createdAt,
    goalAchieved = goalAchieved
)