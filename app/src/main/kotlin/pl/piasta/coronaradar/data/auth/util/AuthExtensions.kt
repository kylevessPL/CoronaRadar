package pl.piasta.coronaradar.data.auth.util

import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

suspend fun LoginManager.registerMCallback(
    manager: CallbackManager
) = suspendCancellableCoroutine<LoginResult> { continuation ->
    val callback = object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult) =
            continuation.resume(result, continuation::resumeWithException)

        override fun onCancel() {
            continuation.cancel()
        }

        override fun onError(error: FacebookException) = continuation.resumeWithException(error)
    }
    registerCallback(manager, callback)
}