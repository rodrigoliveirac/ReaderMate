package com.rodcollab.readermate.core.network

import com.rodcollab.readermate.core.network.model.BookDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class ReaderMateApiImpl(private val httpClient: HttpClient) : ReaderMateApi {
    override suspend fun getAllBooks(): List<BookDto> = httpClient.get<List<BookDto>>("")
}