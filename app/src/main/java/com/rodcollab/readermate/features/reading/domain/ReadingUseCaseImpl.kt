package com.rodcollab.readermate.features.reading.domain

import com.rodcollab.readermate.core.common.ResultOf
import com.rodcollab.readermate.core.model.Reading
import com.rodcollab.readermate.features.reading.data.ReadingRepository

class CurrentReadingUseCaseImpl(
    private val readingRepository: ReadingRepository
) : ReadingUseCase {
    override suspend fun invoke(currentReading: Reading): ResultOf<Reading> {
        return try {
            if(currentReading.uuid.isEmpty()) {
                readingRepository.addReading(reading = currentReading)
                ResultOf.OnSuccess(currentReading)
            } else {
                readingRepository.updateReading(reading = currentReading)
                ResultOf.OnSuccess(currentReading)
            }
        } catch (e:Exception) {
            ResultOf.OnFailure(message = e.message)
        }
    }
}