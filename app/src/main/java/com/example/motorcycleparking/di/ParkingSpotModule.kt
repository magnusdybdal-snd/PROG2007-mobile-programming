package com.example.motorcycleparking.di

import com.example.motorcycleparking.data.FirebaseRepo
import com.example.motorcycleparking.data.repositories.ParkingSpotRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.motorcycleparking.domain.repositories.ParkingSpotRepository

@Module
@InstallIn(SingletonComponent::class)
object ParkingSpotModule {

    @Provides
    @Singleton
    fun provideParkingSpotRepository(
        api: FirebaseRepo
    ): ParkingSpotRepository = ParkingSpotRepositoryImpl(api)
}