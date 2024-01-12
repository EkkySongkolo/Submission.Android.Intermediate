package com.rizki.submissionandroidintermediate.di

import android.content.Context
import com.rizki.submissionandroidintermediate.api.APIConfig
import com.rizki.submissionandroidintermediate.database.StoryDatabase
import com.rizki.submissionandroidintermediate.repository.MainRepository

object Injection {
    fun provideRepository(context: Context): MainRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = APIConfig.getApiService()
        return MainRepository(database, apiService)
    }
}