package pl.piasta.coronaradar.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.piasta.coronaradar.BuildConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Provides
    @Singleton
    fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideAnalytics(@ApplicationContext ctx: Context): FirebaseAnalytics =
        FirebaseAnalytics.getInstance(ctx).apply {
            setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
        }

    @Provides
    @Singleton
    fun provideDynamicLinks(): FirebaseDynamicLinks = FirebaseDynamicLinks.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseModelDownloader(): FirebaseModelDownloader =
        FirebaseModelDownloader.getInstance()
}