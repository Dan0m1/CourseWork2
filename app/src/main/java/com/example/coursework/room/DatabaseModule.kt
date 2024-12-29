package com.example.coursework.room

import android.content.Context
import androidx.room.Room
import com.example.coursework.network.CalendarAppApiService
import com.example.coursework.room.daos.CycleDao
import com.example.coursework.room.daos.DayDao
import com.example.coursework.room.repositories.cycle.CycleRepoImpl
import com.example.coursework.room.repositories.cycle.CycleRepository
import com.example.coursework.room.repositories.day.DayRepoImpl
import com.example.coursework.room.repositories.day.DayRepository
import com.example.coursework.room.repositories.sync.SyncRepoImpl
import com.example.coursework.room.repositories.sync.SyncRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDayDao(database: AppDatabase): DayDao = database.getDaysDao()

    @Provides
    @Singleton
    fun provideDayRepository(
        dayDao: DayDao,
        backendApi: CalendarAppApiService,
        @ApplicationContext context: Context
    ): DayRepository = DayRepoImpl(dayDao, backendApi, context)

    @Provides
    @Singleton
    fun provideCycleDao(database: AppDatabase): CycleDao = database.getCycleDao()

    @Provides
    @Singleton
    fun provideCycleRepository(
        cycleDao: CycleDao,
        @ApplicationContext context: Context
    ): CycleRepository = CycleRepoImpl(cycleDao, context)

    @Provides
    @Singleton
    fun provideSyncRepository(
        dayDao: DayDao,
        cycleDao: CycleDao,
        backendApi: CalendarAppApiService,
        @ApplicationContext context: Context
    ): SyncRepository = SyncRepoImpl(dayDao, cycleDao, backendApi, context)
}