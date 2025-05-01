package com.rodcollab.readermate.features.reading.domain

import com.rodcollab.readermate.core.common.ResultOf
import com.rodcollab.readermate.core.model.Reading

interface ReadingUseCase {
    suspend operator fun invoke(currentReading: Reading): ResultOf<Reading>
}