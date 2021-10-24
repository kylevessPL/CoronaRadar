package pl.piasta.coronaradar.data.auth.model

sealed class ActionCode {

    data class PasswordReset(val oob: String) : ActionCode()
    data class VerifyEmail(val oob: String) : ActionCode()
    object None : ActionCode()
}
