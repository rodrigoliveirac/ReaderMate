package com.rodcollab.readermate.core.common

sealed class ResultOf<out T> {
    data class OnSuccess<out R>(val value: R): ResultOf<R>()
    data class OnFailure(
        val message: String?,
    ): ResultOf<Nothing>()
}