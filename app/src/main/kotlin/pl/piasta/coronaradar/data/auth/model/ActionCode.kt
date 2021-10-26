package pl.piasta.coronaradar.data.auth.model

sealed class ActionCode(val oob: String) {

    data class PasswordReset(private val passwordResetCode: String) : ActionCode(passwordResetCode)
    data class VerifyEmail(private val verifyEmailCode: String) : ActionCode(verifyEmailCode)
}
