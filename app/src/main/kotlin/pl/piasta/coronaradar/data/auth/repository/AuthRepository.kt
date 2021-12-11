package pl.piasta.coronaradar.data.auth.repository

import android.net.Uri
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import pl.piasta.coronaradar.data.auth.model.ActionCode
import pl.piasta.coronaradar.data.auth.model.UserDetails
import pl.piasta.coronaradar.util.ResultState

interface AuthRepository {

    val currentUser: UserDetails?

    fun login(email: String, password: String): Flow<ResultState<FirebaseUser>>
    fun register(email: String, password: String): Flow<ResultState<FirebaseUser>>
    fun logout(): Flow<ResultState<Nothing>>
    fun sendPasswordResetEmail(email: String): Flow<ResultState<Boolean>>
    fun sendVerificationEmail(): Flow<ResultState<Nothing>>
    fun loginWithGoogle(task: Task<GoogleSignInAccount>): Flow<ResultState<FirebaseUser>>
    fun loginWithFacebook(callbackManager: CallbackManager): Flow<ResultState<FirebaseUser>>
    fun verifyActionCode(data: Uri): Flow<ResultState<ActionCode?>>
    fun verifyEmail(actionCode: String): Flow<ResultState<Nothing>>
    fun resetPassword(actionCode: String, newPassword: String): Flow<ResultState<Nothing>>
    fun updateCurrentUserPassword(password: String): Flow<ResultState<Nothing>>
    fun updateCurrentUserDetails(
        displayName: String,
        avatarUri: Uri? = null
    ): Flow<ResultState<Nothing>>

    fun uploadAvatar(byteArray: ByteArray): Flow<ResultState<Uri>>
}