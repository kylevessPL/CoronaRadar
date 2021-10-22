package pl.piasta.coronaradar.data.auth.repository

import android.util.Log
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import pl.piasta.coronaradar.data.auth.exception.EmailNotVerifiedException
import pl.piasta.coronaradar.data.auth.util.registerMCallback
import pl.piasta.coronaradar.util.ResultState
import pl.piasta.coronaradar.util.TAG
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(private val auth: FirebaseAuth) : AuthRepository {

    override fun login(email: String, password: String): Flow<ResultState<FirebaseUser>> = flow {
        emit(ResultState.Loading)
        val result = auth.signInWithEmailAndPassword(email, password).await()
        verificationCheck(result.user!!)
        Log.d(this@FirebaseAuthRepository.TAG, "login:success")
        emit(ResultState.Success(result.user!!))
    }.catch { ex ->
        Log.d(this@FirebaseAuthRepository.TAG, "login:failure")
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
        Log.d(this@FirebaseAuthRepository.TAG, "register:failure")
        emit(ResultState.Error(ex))
    }.flowOn(Dispatchers.IO)

    override fun passwordResetEmail(email: String): Flow<ResultState<Boolean>> = flow {
        emit(ResultState.Loading)
        auth.sendPasswordResetEmail(email).await()
        Log.d(this@FirebaseAuthRepository.TAG, "passwordResetEmail:success")
        emit(ResultState.Success(true))
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "passwordResetEmail:failure", ex)
        when (ex) {
            is FirebaseAuthInvalidUserException -> emit(ResultState.Success(false))
            else -> emit(ResultState.Error(ex))
        }
    }.flowOn(Dispatchers.IO)

    override fun verificationEmail(): Flow<ResultState<Nothing>> = flow {
        emit(ResultState.Loading)
        auth.currentUser!!.sendEmailVerification().await()
        Log.d(this@FirebaseAuthRepository.TAG, "verificationEmail:success")
        emit(ResultState.Success())
    }.catch { ex ->
        Log.w(this@FirebaseAuthRepository.TAG, "verificationEmail:failure", ex)
        emit(ResultState.Error(ex))
    }.flowOn(Dispatchers.IO)

    override fun googleLogin(task: Task<GoogleSignInAccount>): Flow<ResultState<FirebaseUser>> =
        flow {
            emit(ResultState.Loading)
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            Log.d(this@FirebaseAuthRepository.TAG, "googleLogin:success")
            emit(ResultState.Success(result.user!!))
        }.catch { ex ->
            Log.d(this@FirebaseAuthRepository.TAG, "googleLogin:failure")
            emit(ResultState.Error(ex))
        }.flowOn(Dispatchers.IO)

    override fun facebookLogin(callbackManager: CallbackManager): Flow<ResultState<FirebaseUser>> =
        flow {
            emit(ResultState.Loading)
            val account = facebookLoginManager().registerMCallback(callbackManager)
            val credential = FacebookAuthProvider.getCredential(account.accessToken.token)
            val result = auth.signInWithCredential(credential).await()
            Log.d(this@FirebaseAuthRepository.TAG, "facebookLogin:success")
            emit(ResultState.Success(result.user!!))
        }.catch { ex ->
            Log.d(this@FirebaseAuthRepository.TAG, "facebookLogin:failure")
            emit(ResultState.Error(ex))
        }.flowOn(Dispatchers.IO)

    private fun verificationCheck(user: FirebaseUser): Boolean =
        user.isEmailVerified.and(false).let { throw EmailNotVerifiedException() }

    private fun facebookLoginManager(): LoginManager = LoginManager.getInstance()
}