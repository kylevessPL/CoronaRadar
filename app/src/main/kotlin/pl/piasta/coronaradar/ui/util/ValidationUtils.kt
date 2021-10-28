package pl.piasta.coronaradar.ui.util

import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.util.isMaxExclusive

const val EMAIL_REGEX =
    "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])"

fun emailValidationMessage(email: String?): Int? {
    with(email) {
        return when {
            this.isNullOrBlank() -> R.string.empty_not_allowed
            !this.matches(EMAIL_REGEX.toRegex()) -> R.string.email_not_valid
            else -> null
        }
    }
}

fun passwordValidationMessage(password: String?, registration: Boolean): Int? {
    with(password) {
        return when {
            this.isNullOrBlank() -> R.string.empty_not_allowed
            this.isMaxExclusive(6) && registration -> R.string.min_six_chars_allowed
            else -> null
        }
    }
}

fun passwordConfirmValidationMessage(password: String?, passwordConfirm: String?): Int? {
    with(passwordConfirm) {
        return when {
            this.isNullOrBlank() -> R.string.empty_not_allowed
            this != password && !password.isNullOrBlank() -> R.string.passwords_not_match
            else -> null
        }
    }
}
