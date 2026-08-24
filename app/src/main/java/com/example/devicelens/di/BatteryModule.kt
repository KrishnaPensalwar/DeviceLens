package com.example.devicelens.di

import com.example.devicelens.data.repository.BatteryRepositoryImpl
import com.example.devicelens.domain.repository.BatteryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BatteryModule {

    @Binds
    @Singleton
    abstract fun bindBatteryRepository(
        implementation: BatteryRepositoryImpl
    ): BatteryRepository
}