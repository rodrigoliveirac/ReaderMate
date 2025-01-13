package com.rodcollab.readermate.app.di

import androidx.room.Room
import com.rodcollab.readermate.core.local.AppDatabase
import com.rodcollab.readermate.core.local.DATABASE_NAME
import com.rodcollab.readermate.core.local.ReaderMateDao
import org.koin.dsl.module

val appModule = module {
    single { this }

    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, DATABASE_NAME).build()
    }

    single<ReaderMateDao> {
        val database = get<AppDatabase>()
        database.readerMateDao()
    }
}