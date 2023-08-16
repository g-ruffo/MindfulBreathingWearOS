package ca.veltus.mindfulbreathingwearos.di

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
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
    fun provideHealthServicesClient(@ApplicationContext context: Context): HealthServicesClient {
      return HealthServices.getClient(context)
    }


    @Provides
    @Singleton
    fun provideBreathingRepository(@ApplicationContext context: Context, healthServicesClient: HealthServicesClient): BreathingRepository {
        return BreathingRepositoryImpl(context, healthServicesClient)
    }
}