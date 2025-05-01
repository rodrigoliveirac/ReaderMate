package com.rodcollab.readermate.features.checkin.domain

import com.rodcollab.readermate.core.model.CheckIn

interface CheckInUseCase {
    suspend operator fun invoke(currentPage: Int = INVALID_CURRENT_PAGE, isCompleted: Boolean = false) : Result<CheckIn>

    companion object {
        private const val INVALID_CURRENT_PAGE = -1
    }
}