package pl.piasta.coronaradar.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MainDispatcher

@InstallIn(SingletonComponent::class)
@Module
class CoroutineDispatchersModule {

    @DefaultDispatcher
    @Provides
    fun providesDefaultDispatcher() = Dispatchers.Default

    @IoDispatcher
    @Provides
    fun providesIoDispatcher() = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun providesMainDispatcher() = Dispatchers.Main
}