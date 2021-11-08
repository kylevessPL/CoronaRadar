package pl.piasta.coronaradar.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import pl.piasta.coronaradar.data.auth.repository.AuthRepository
import pl.piasta.coronaradar.data.auth.repository.FirebaseAuthRepository
import pl.piasta.coronaradar.data.ml.repository.FirebaseMlRepository
import pl.piasta.coronaradar.data.ml.repository.MlRepository

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun provideAuthRepository(firebaseAuthRepository: FirebaseAuthRepository): AuthRepository

    @Binds
    @ViewModelScoped
    abstract fun provideMlRepository(firebaseMlRepository: FirebaseMlRepository): MlRepository
}