package pl.piasta.coronaradar.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.DESCENDING
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
import pl.piasta.coronaradar.data.util.*
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserHistoryCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GetAllUserHistoryPagingQuery

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SurveysCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GetAllSurveysPagingQuery

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
    @UserHistoryCollection
    fun provideUserHistoryCollection() = Firebase.firestore
        .collection(USERS)
        .document(Firebase.auth.uid.toString())
        .collection(HISTORY)

    @Provides
    @GetAllUserHistoryPagingQuery
    fun provideGetAllUserHistoryPagingQuery() = Firebase.firestore
        .collection(USERS)
        .document(Firebase.auth.uid.toString())
        .collection(HISTORY)
        .orderBy(DATE, DESCENDING)
        .limit(PAGE_SIZE.toLong())

    @Provides
    @SurveysCollection
    fun provideSurveysCollection() = Firebase.firestore.collection(SURVEYS)

    @Provides
    @GetAllSurveysPagingQuery
    fun provideSurveysPagingQuery() = Firebase.firestore
        .collection(SURVEYS)
        .orderBy(DATE, DESCENDING)
        .limit(PAGE_SIZE.toLong())
}