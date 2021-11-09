package pl.piasta.coronaradar.di

import com.google.mlkit.common.model.CustomRemoteModel
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.common.model.RemoteModelSource
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MlModule {

    @Provides
    @Singleton
    fun provideRemoteModelManager(): RemoteModelManager = RemoteModelManager.getInstance()

    @Provides
    @Singleton
    fun provideRemoteModel(remoteModelSource: RemoteModelSource): CustomRemoteModel =
        CustomRemoteModel.Builder(remoteModelSource).build()

    @Provides
    @Singleton
    fun provideCustomImageLabelerOptions(remoteModel: CustomRemoteModel) =
        CustomImageLabelerOptions.Builder(remoteModel)
            .setMaxResultCount(1)
            .build()

    @Provides
    @Singleton
    fun provideImageLabeler(labelerOptions: CustomImageLabelerOptions) =
        ImageLabeling.getClient(labelerOptions)
}