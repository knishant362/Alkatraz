package com.trendster.harpic.di

import com.trendster.api.HarperClient
import com.trendster.api.services.HarperAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideApiService(): HarperAPI {
        return HarperClient().retrofit.create(HarperAPI::class.java)
    }
}
