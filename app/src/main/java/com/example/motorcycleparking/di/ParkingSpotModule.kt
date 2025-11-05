package com.example.motorcycleparking.di

import com.example.motorcycleparking.data.repositories.ParkingSpotRepositoryImpl
import com.example.motorcycleparking.domain.repositories.ParkingSpotRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Binds

@Module
@InstallIn(SingletonComponent::class)
abstract class ParkingSpotModule {

    @Binds
    @Singleton
    abstract fun bindParkingSpotRepository(
        impl: ParkingSpotRepositoryImpl
    ): ParkingSpotRepository
}