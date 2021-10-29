package pl.piasta.coronaradar.data.auth.repository

import android.net.Uri
import android.util.Log
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ActionCodeResult
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import pl.piasta.coronaradar.data.auth.exception.EmailNotVerifiedException
import pl.piasta.coronaradar.data.auth.model.ActionCode
import pl.piasta.coronaradar.data.auth.model.ActionCode.PasswordReset
import pl.piasta.coronaradar.data.auth.model.ActionCode.VerifyEmail
import pl.piasta.coronaradar.data.auth.util.registerMCallback
import pl.piasta.coronaradar.di.ResetPasswordEmailSettings
import pl.piasta.coronaradar.di.VerificationEmailSettings
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.TAG
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val dynamicLinks: FirebaseDynamicLinks,
    private val googleSignInClient: GoogleSignInClient,
    private val facebookLoginManager: LoginManager,
    @VerificationEmailSettings private val verificationEmailActionCodeSettings: ActionCodeSettings,
    @ResetPasswordEmailSettings private val resetPasswordEmailActionCodeSettings: ActionCodeSettings,
) : AuthRepository {

    override fun login(email: String, password: String): Flow<ResultState<FirebaseUser>> = flow {
        emit(ResultState.Loading)
        val result = auth.signInWithEmailAndPassword(email, password).await()
        verificationCheck(result.user!!)
        Log.d(this@FirebaseAuthRepository.TAG, "login:success")
        emit(ResultState.Success(result.user!!))
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "login:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(Dispatchers.IO)

    override fun register(
        email: String,
        password: String
    ): Flow<ResultState<FirebaseUser>> = flow {
        emit(ResultState.Loading)
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        Log.d(this@FirebaseAuthRepository.TAG, "register:success")
        emit(ResultState.Success(result.user!!))
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "register:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(Dispatchers.IO)

    override fun logout(): Flow<ResultState<Nothing>> = flow {
        emit(ResultState.Loading)
        auth.signOut()
        googleSignInClient.signOut().await()
        facebookLoginManager.logOut()
        Log.d(this@FirebaseAuthRepository.TAG, "logout:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "logout:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(Dispatchers.IO)

    override fun sendPasswordResetEmail(email: String): Flow<ResultState<Boolean>> = flow {
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
    }.flowOn(Dispatchers.IO)

    override fun sendVerificationEmail(): Flow<ResultState<Nothing>> = flow {
        emit(ResultState.Loading)
        auth.currentUser!!.sendEmailVerification(verificationEmailActionCodeSettings).await()
        Log.d(this@FirebaseAuthRepository.TAG, "sendVerificationEmail:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "sendVerificationEmail:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(Dispatchers.IO)

    override fun loginWithGoogle(task: Task<GoogleSignInAccount>): Flow<ResultState<FirebaseUser>> =
        flow {
            emit(ResultState.Loading)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            Log.d(this@FirebaseAuthRepository.TAG, "loginWithGoogle:success")
            emit(ResultState.Success(result.user!!))
        }.catch { ex ->
            Log.w(this@FirebaseAuthRepository.TAG, "loginWithGoogle:failure", ex)
            emit(ResultState.Error(ex))
        }.flowOn(Dispatchers.IO)

    override fun loginWithFacebook(callbackManager: CallbackManager): Flow<ResultState<FirebaseUser>> =
        flow {
            emit(ResultState.Loading)
            val account = facebookLoginManager.registerMCallback(callbackManager)
            val credential = FacebookAuthProvider.getCredential(account.accessToken.token)
            val result = auth.signInWithCredential(credential).await()
            Log.d(this@FirebaseAuthRepository.TAG, "loginWithFacebook:success")
            emit(ResultState.Success(result.user!!))
        }.catch { ex ->
            Log.w(this@FirebaseAuthRepository.TAG, "loginWithFacebook:failure", ex)
            emit(ResultState.Error(ex))
        }.flowOn(Dispatchers.IO)

    override fun verifyActionCode(data: Uri): Flow<ResultState<ActionCode?>> = flow {
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
    }.flowOn(Dispatchers.IO)

    override fun verifyEmail(actionCode: String): Flow<ResultState<Nothing>> = flow {
        emit(ResultState.Loading)
        auth.applyActionCode(actionCode).await()
        auth.currentUser?.reload()?.await()
        Log.d(this@FirebaseAuthRepository.TAG, "verifyEmail:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "verifyEmail:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(Dispatchers.IO)

    override fun resetPassword(
        actionCode: String,
        newPassword: String
    ): Flow<ResultState<Nothing>> = flow {
        emit(ResultState.Loading)
        auth.confirmPasswordReset(actionCode, newPassword).await()
        Log.d(this@FirebaseAuthRepository.TAG, "resetPassword:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "resetPassword:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(Dispatchers.IO)

    private fun toActionCode(
        oob: String,
        operation: Int
    ) = when (operation) {
        ActionCodeResult.PASSWORD_RESET -> PasswordReset(oob)
        ActionCodeResult.VERIFY_EMAIL -> VerifyEmail(oob)
        else -> null
    }

    private fun verificationCheck(user: FirebaseUser): Boolean =
        user.isEmailVerified.and(false).let { throw EmailNotVerifiedException() }
}