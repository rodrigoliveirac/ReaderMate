package com.rodcollab.readermate.features.checkin.domain

import com.rodcollab.readermate.core.common.ResultOf
import com.rodcollab.readermate.core.model.CheckIn

interface CheckInUseCase {
    suspend operator fun invoke(totalPages: Int) : ResultOf<CheckIn>
}