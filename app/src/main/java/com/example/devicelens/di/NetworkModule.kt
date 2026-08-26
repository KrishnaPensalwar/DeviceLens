package com.example.devicelens.di

import com.example.devicelens.data.repository.NetworkRepositoryImpl
import com.example.devicelens.domain.repository.NetworkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindNetworkRepository(
        implementation: NetworkRepositoryImpl
    ): NetworkRepository
}
