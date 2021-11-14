package pl.piasta.coronaradar.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.google.firebase.ml.modeldownloader.ktx.modelDownloader
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.piasta.coronaradar.BuildConfig
import pl.piasta.coronaradar.data.util.HISTORY
import pl.piasta.coronaradar.data.util.PAGE_SIZE
import pl.piasta.coronaradar.data.util.SURVEYS
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GetAllHistoryQuery

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GetAllSurveysQuery

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Provides
    @Singleton
    fun provideAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideStorage(): FirebaseStorage = Firebase.storage

    @Provides
    @Singleton
    fun provideAnalytics(): FirebaseAnalytics =
        Firebase.analytics.apply {
            setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
        }

    @Provides
    @Singleton
    fun provideDynamicLinks(): FirebaseDynamicLinks = Firebase.dynamicLinks

    @Provides
    @Singleton
    fun provideFirebaseModelDownloader(): FirebaseModelDownloader = Firebase.modelDownloader

    @Provides
    @Singleton
    @GetAllHistoryQuery
    fun provideGetAllHistoryPagingQuery() = Firebase.firestore
        .collection(HISTORY)
        .limit(PAGE_SIZE.toLong())

    @Provides
    @Singleton
    @GetAllSurveysQuery
    fun provideGetAllSurveysPagingQuery() = Firebase.firestore
        .collection(SURVEYS)
        .limit(PAGE_SIZE.toLong())
}