package com.example.devicelens.di

import com.example.devicelens.data.system.DeviceInfoProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDeviceInfoProvider(): DeviceInfoProvider {
        return DeviceInfoProvider()
    }
}