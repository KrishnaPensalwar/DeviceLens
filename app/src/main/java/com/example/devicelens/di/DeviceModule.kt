package com.example.devicelens.di

import com.example.devicelens.data.repository.DeviceRepositoryImpl
import com.example.devicelens.domain.repository.DeviceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DeviceModule {

    @Binds
    @Singleton
    abstract fun bindDeviceRepository(
        repositoryImpl: DeviceRepositoryImpl
    ): DeviceRepository
}