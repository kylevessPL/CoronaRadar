package pl.piasta.coronaradar.ui.common.viewmodel

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseUserLiveData : LiveData<FirebaseUser?>(), FirebaseAuth.IdTokenListener {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onIdTokenChanged(auth: FirebaseAuth) = updateUser(auth)

    init {
        updateUser(auth)
    }

    override fun onActive() {
        super.onActive()
        auth.addIdTokenListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        auth.removeIdTokenListener(this)
    }

    private fun updateUser(auth: FirebaseAuth) {
        val user = auth.currentUser
        val providerData = user?.providerData?.filterNot { it.providerId == "firebase" }
        value = when (providerData?.size == 1 && providerData.first().providerId == "password") {
            true -> user?.takeIf { it.isEmailVerified }
            else -> user
        }
    }
}