package com.example.coursework.data

import com.example.coursework.network.NetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {
    @Provides
    @Singleton
    fun provideLoginRepository(): LoginRepository {
        return NetworkLoginRepository(networkApiService = NetworkModule.provideApiService())
    }
}