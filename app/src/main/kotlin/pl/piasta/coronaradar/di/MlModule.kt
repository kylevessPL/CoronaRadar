package pl.piasta.coronaradar.di

import com.google.mlkit.common.model.CustomRemoteModel
import com.google.mlkit.common.model.RemoteModel
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.linkfirebase.FirebaseModelSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.piasta.coronaradar.BuildConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MlModule {

    @Provides
    @Singleton
    fun provideRemoteModelManager(): RemoteModelManager = RemoteModelManager.getInstance()

    @Provides
    @Singleton
    fun provideRemoteModel(): RemoteModel = CustomRemoteModel.Builder(
        FirebaseModelSource.Builder(BuildConfig.ML_MODEL_NAME).build()
    ).build()
}