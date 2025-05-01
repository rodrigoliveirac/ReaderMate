package com.rodcollab.readermate.core.model

fun ReadingEntity.toReading() = Reading(
    uuid = uuid,
    bookRecordId = bookRecordId,
    goalPerDay = goalPerDay,
    isCurrent = isCurrent,
    startedDate = startedDate,
    numberOfSessionsLeft = estimatedEndInNumberOfSessions,
    isCompleted = isCompleted,
    currentPage = currentPage
)

fun Reading.toReadingEntity() = ReadingEntity(
    uuid = uuid,
    bookRecordId = bookRecordId,
    goalPerDay = goalPerDay,
    isCurrent = isCurrent,
    startedDate = startedDate,
    estimatedEndInNumberOfSessions = numberOfSessionsLeft,
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

fun BookEntity.toBookRecord() = BookRecord(
    uuid = uuid,
    title = title,
    author = author,
    description = description,
    totalPages = totalPages,
)

fun BookRecord.toBookEntity() = BookEntity(
    uuid = uuid,
    title = title,
    author = author,
    description = description,
    totalPages = totalPages,
)