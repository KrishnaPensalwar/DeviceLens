package com.example.devicelens.di

import com.example.devicelens.data.repository.UsageRepositoryImpl
import com.example.devicelens.domain.repository.UsageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UsageModule {

    @Binds
    @Singleton
    abstract fun bindUsageRepository(
        implementation: UsageRepositoryImpl
    ): UsageRepository
}
