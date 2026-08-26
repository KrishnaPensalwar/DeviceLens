package com.example.devicelens.di

import com.example.devicelens.data.repository.StorageRepositoryImpl
import com.example.devicelens.domain.repository.StorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    @Singleton
    abstract fun bindStorageRepository(
        implementation: StorageRepositoryImpl
    ): StorageRepository
}
