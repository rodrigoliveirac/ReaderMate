package com.rodcollab.readermate.app.di

import android.util.Log
import com.rodcollab.readermate.core.network.ReaderMateApi
import com.rodcollab.readermate.core.network.ReaderMateApiImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.DefaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val ktorModule = module {
    single { this }

    single {
        HttpClient(Android) {
            install(JsonFeature) {
                KotlinxSerializer(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })

                engine {
                    connectTimeout = 6000
                    socketTimeout = 6000
                }

                install(ResponseObserver) {
                    onResponse { response ->
                        Log.d("HTTP status:", "${response.status.value}")
                    }
                }

                install(DefaultRequest) {
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                }
            }
        }
    }

    factory<ReaderMateApi> {
        ReaderMateApiImpl(httpClient = get())
    }
}