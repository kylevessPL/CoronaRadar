package pl.piasta.coronaradar.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.chaquo.python.Python
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.ktx.actionCodeSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.piasta.coronaradar.BuildConfig
import pl.piasta.coronaradar.R
import splitties.resources.str
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VerificationEmailSettings

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ResetPasswordEmailSettings

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providePythonInterpreter() = Python.getInstance()

    @Provides
    @Singleton
    fun providePrefs(@ApplicationContext ctx: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(ctx)

    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext ctx: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(ctx.str(R.string.google_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(ctx, gso)
    }

    @Provides
    @Singleton
    fun provideFacebookLoginManager(): LoginManager = LoginManager.getInstance()

    @Provides
    @Singleton
    @VerificationEmailSettings
    fun provideVerificationEmailActionCodeSettings(): ActionCodeSettings =
        actionCodeSettings {
            url = "https://" + BuildConfig.SERVER_DOMAIN + "/auth/verify"
            dynamicLinkDomain = BuildConfig.SERVER_DOMAIN
            handleCodeInApp = true
            setAndroidPackageName(BuildConfig.APPLICATION_ID, false, null)
        }

    @Provides
    @Singleton
    @ResetPasswordEmailSettings
    fun provideResetPasswordEmailActionCodeSettings(): ActionCodeSettings =
        actionCodeSettings {
            url = "https://" + BuildConfig.SERVER_DOMAIN + "/auth/reset-password"
            dynamicLinkDomain = BuildConfig.SERVER_DOMAIN
            handleCodeInApp = true
            setAndroidPackageName(BuildConfig.APPLICATION_ID, false, null)
        }
}