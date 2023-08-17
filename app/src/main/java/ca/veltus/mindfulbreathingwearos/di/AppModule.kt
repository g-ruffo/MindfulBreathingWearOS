package ca.veltus.mindfulbreathingwearos.di

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import androidx.room.Room
import ca.veltus.mindfulbreathingwearos.common.Constants
import ca.veltus.mindfulbreathingwearos.data.local.HeartRateDAO
import ca.veltus.mindfulbreathingwearos.data.local.HeartRateDatabase
import ca.veltus.mindfulbreathingwearos.data.repository.BreathingRepositoryImpl
import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHeartRateDatabase(@ApplicationContext context: Context): HeartRateDatabase {
        return Room.databaseBuilder(
            context,
            HeartRateDatabase::class.java,
            Constants.DATABASE
        ).build()
    }

    @Provides
    @Singleton
    fun provideHeartRateDAO(db: HeartRateDatabase): HeartRateDAO {
        return db.dao
    }

    @Provides
    @Singleton
    fun provideHealthServicesClient(@ApplicationContext context: Context): HealthServicesClient {
      return HealthServices.getClient(context)
    }


    @Provides
    @Singleton
    fun provideBreathingRepository(
        dao: HeartRateDAO,
        healthServicesClient: HealthServicesClient
    ): BreathingRepository {
        return BreathingRepositoryImpl(dao, healthServicesClient)
    }
}