package pl.piasta.coronaradar.ui.common.viewmodel

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseUserLiveData : LiveData<FirebaseUser?>(), FirebaseAuth.AuthStateListener {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        val user = auth.currentUser
        value = when (user?.providerId != "password") {
            true -> user
            else -> user?.takeIf { it.isEmailVerified }
        }
    }

    override fun onActive() {
        super.onActive()
        auth.addAuthStateListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        auth.removeAuthStateListener(this)
    }
}