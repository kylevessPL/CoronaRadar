package pl.piasta.coronaradar.data.auth.repository

import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import pl.piasta.coronaradar.util.ResultState

interface AuthRepository {

    fun login(email: String, password: String): Flow<ResultState<FirebaseUser>>
    fun register(email: String, password: String): Flow<ResultState<FirebaseUser>>
    fun passwordResetEmail(email: String): Flow<ResultState<Boolean>>
    fun verificationEmail(): Flow<ResultState<Nothing>>
    fun googleLogin(task: Task<GoogleSignInAccount>): Flow<ResultState<FirebaseUser>>
    fun facebookLogin(callbackManager: CallbackManager): Flow<ResultState<FirebaseUser>>
}