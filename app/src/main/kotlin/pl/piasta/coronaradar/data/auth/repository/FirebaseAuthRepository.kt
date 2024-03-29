package pl.piasta.coronaradar.data.auth.repository

import android.net.Uri
import android.util.Log
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.ActionCodeResult.*
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import pl.piasta.coronaradar.data.auth.exception.EmailNotVerifiedException
import pl.piasta.coronaradar.data.auth.model.ActionCode.PasswordReset
import pl.piasta.coronaradar.data.auth.model.ActionCode.VerifyEmail
import pl.piasta.coronaradar.data.auth.model.UserDetails
import pl.piasta.coronaradar.data.auth.util.registerMCallback
import pl.piasta.coronaradar.di.IoDispatcher
import pl.piasta.coronaradar.di.ResetPasswordEmailSettings
import pl.piasta.coronaradar.di.VerificationEmailSettings
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.TAG
import pl.piasta.coronaradar.util.ifNullOrEmpty
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val dynamicLinks: FirebaseDynamicLinks,
    private val googleSignInClient: GoogleSignInClient,
    private val facebookLoginManager: LoginManager,
    @VerificationEmailSettings private val verificationEmailActionCodeSettings: ActionCodeSettings,
    @ResetPasswordEmailSettings private val resetPasswordEmailActionCodeSettings: ActionCodeSettings
) : AuthRepository {

    override val currentUser = auth.currentUser?.run {
        UserDetails(displayName.ifNullOrEmpty { "user_".plus(uid) }, email!!, photoUrl)
    }

    override fun login(email: String, password: String) = flow {
        emit(ResultState.Loading)
        val result = auth.signInWithEmailAndPassword(email, password).await()
        verificationCheck(result.user!!)
        Log.d(this@FirebaseAuthRepository.TAG, "login:success")
        emit(ResultState.Success(result.user!!))
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "login:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(coroutineDispatcher)

    override fun register(
        email: String,
        password: String
    ) = flow {
        emit(ResultState.Loading)
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        Log.d(this@FirebaseAuthRepository.TAG, "register:success")
        emit(ResultState.Success(result.user!!))
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "register:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(coroutineDispatcher)

    override fun logout() = flow {
        emit(ResultState.Loading)
        auth.signOut()
        googleSignInClient.signOut().await()
        facebookLoginManager.logOut()
        Log.d(this@FirebaseAuthRepository.TAG, "logout:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "logout:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(coroutineDispatcher)

    override fun sendPasswordResetEmail(email: String) = flow {
        emit(ResultState.Loading)
        auth.sendPasswordResetEmail(email, resetPasswordEmailActionCodeSettings).await()
        Log.d(this@FirebaseAuthRepository.TAG, "sendPasswordResetEmail:success")
        emit(ResultState.Success(true))
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "sendPasswordResetEmail:failure", ex)
        when (ex) {
            is FirebaseAuthInvalidUserException -> emit(ResultState.Success(false))
            else -> emit(ResultState.Error(ex))
        }
    }.flowOn(coroutineDispatcher)

    override fun sendVerificationEmail() = flow {
        emit(ResultState.Loading)
        auth.currentUser!!.sendEmailVerification(verificationEmailActionCodeSettings).await()
        Log.d(this@FirebaseAuthRepository.TAG, "sendVerificationEmail:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "sendVerificationEmail:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(coroutineDispatcher)

    override fun loginWithGoogle(task: Task<GoogleSignInAccount>) = flow {
        emit(ResultState.Loading)
        val account = task.getResult(ApiException::class.java)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val result = auth.signInWithCredential(credential).await()
        Log.d(this@FirebaseAuthRepository.TAG, "loginWithGoogle:success")
        emit(ResultState.Success(result.user!!))
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "loginWithGoogle:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(coroutineDispatcher)

    override fun loginWithFacebook(callbackManager: CallbackManager) = flow {
        emit(ResultState.Loading)
        val account = facebookLoginManager.registerMCallback(callbackManager)
        val credential = FacebookAuthProvider.getCredential(account.accessToken.token)
        val result = auth.signInWithCredential(credential).await()
        Log.d(this@FirebaseAuthRepository.TAG, "loginWithFacebook:success")
        emit(ResultState.Success(result.user!!))
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "loginWithFacebook:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(coroutineDispatcher)

    override fun verifyActionCode(data: Uri) = flow {
        emit(ResultState.Loading)
        val dynamicLink = dynamicLinks.getDynamicLink(data).await()
        val oob = dynamicLink.link?.getQueryParameter("oobCode")
        val result = oob?.let { auth.checkActionCode(it).await() }
        val actionCode = result?.let { toActionCode(oob, it.operation) }
        Log.d(this@FirebaseAuthRepository.TAG, "verifyActionCode:success")
        emit(ResultState.Success(actionCode))
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "verifyActionCode:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(coroutineDispatcher)

    override fun verifyEmail(actionCode: String) = flow {
        emit(ResultState.Loading)
        auth.applyActionCode(actionCode).await()
        reloadCurrentUserData()
        Log.d(this@FirebaseAuthRepository.TAG, "verifyEmail:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "verifyEmail:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(coroutineDispatcher)

    override fun resetPassword(
        actionCode: String,
        newPassword: String
    ) = flow {
        emit(ResultState.Loading)
        auth.confirmPasswordReset(actionCode, newPassword).await()
        Log.d(this@FirebaseAuthRepository.TAG, "resetPassword:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "resetPassword:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(coroutineDispatcher)

    override fun updateCurrentUserPassword(password: String) = flow {
        emit(ResultState.Loading)
        auth.currentUser!!.updatePassword(password).await()
        Log.d(this@FirebaseAuthRepository.TAG, "updateCurrentUserPassword:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "updateCurrentUserPassword:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(coroutineDispatcher)

    override fun updateCurrentUserDetails(displayName: String, avatarUri: Uri?) = flow {
        emit(ResultState.Loading)
        val request = UserProfileChangeRequest.Builder().apply {
            this.displayName = displayName
            this.photoUri = avatarUri
        }.build()
        auth.currentUser!!.updateProfile(request).await()
        reloadCurrentUserData()
        Log.d(this@FirebaseAuthRepository.TAG, "updateCurrentUserDetails:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "updateCurrentUserDetails:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(coroutineDispatcher)

    override fun uploadAvatar(byteArray: ByteArray) = flow {
        emit(ResultState.Loading)
        val ref = storage.reference.child("avatars/${auth.currentUser!!.uid}")
        ref.putBytes(byteArray).await()
        val storageUri = ref.downloadUrl.await()
        Log.d(this@FirebaseAuthRepository.TAG, "uploadAvatar:success")
        emit(ResultState.Success(storageUri))
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "uploadAvatar:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(coroutineDispatcher)

    private suspend fun reloadCurrentUserData() {
        auth.currentUser?.reload()?.await()
        auth.currentUser?.getIdToken(true)?.await()
    }

    private fun toActionCode(
        oob: String,
        operation: Int
    ) = when (operation) {
        PASSWORD_RESET -> PasswordReset(oob)
        VERIFY_EMAIL -> VerifyEmail(oob)
        else -> null
    }

    private fun verificationCheck(user: FirebaseUser): Boolean =
        user.isEmailVerified.and(false).let { throw EmailNotVerifiedException() }
}