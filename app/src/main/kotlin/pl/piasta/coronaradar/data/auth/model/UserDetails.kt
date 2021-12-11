package pl.piasta.coronaradar.data.auth.model

import android.net.Uri

data class UserDetails(val displayName: String?, val email: String?, val avatarUri: Uri? = null)