package com.alexplas.weathernew.di

import android.content.Context
import androidx.room.Room
import com.alexplas.weathernew.data.local.dao.CityDao
import com.alexplas.weathernew.data.local.dao.LocalDao
import com.alexplas.weathernew.data.local.db.CityDatabase
import com.alexplas.weathernew.data.local.db.LocalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideCityDb(@ApplicationContext context: Context): CityDatabase {
        return Room.databaseBuilder(
            context,
            CityDatabase::class.java,
            CityDatabase.DATABASE_NAME
        )
            .createFromAsset("cities.db")
            .fallbackToDestructiveMigration() // Add this line to allow for destructive migrations
            .build()
    }

    @Singleton
    @Provides
    fun provideCityDao(cityDatabase: CityDatabase): CityDao {
        return cityDatabase.cityDao()
    }

    @Singleton
    @Provides
    fun provideLocalDb(@ApplicationContext context: Context): LocalDatabase {
        return Room.databaseBuilder(
            context,
            LocalDatabase::class.java,
            LocalDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideLocalDao(localDatabase: LocalDatabase): LocalDao {
        return localDatabase.localDao()
    }
}