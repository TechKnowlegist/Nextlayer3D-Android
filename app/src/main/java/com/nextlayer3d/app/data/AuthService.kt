package com.nextlayer3d.app.data

import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.step.AuthSignUpStep
import com.amplifyframework.kotlin.core.Amplify

/** Thin wrapper around Amplify.Auth (Kotlin coroutines facade) against the
 * same Cognito user pool the website uses (email as username, email
 * verification) — signing up here creates the exact same kind of account
 * as signing up on nextlayer3d.app. */
object AuthService {

    suspend fun signUp(email: String, password: String): Boolean {
        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), email)
            .build()
        val result = Amplify.Auth.signUp(email, password, options)
        if (result.nextStep.signUpStep == AuthSignUpStep.CONFIRM_SIGN_UP_STEP) {
            return false // needs the emailed confirmation code
        }
        return result.isSignUpComplete
    }

    suspend fun confirmSignUp(email: String, code: String): Boolean {
        val result = Amplify.Auth.confirmSignUp(email, code)
        return result.isSignUpComplete
    }

    suspend fun signIn(email: String, password: String): Boolean {
        val result = Amplify.Auth.signIn(email, password)
        return result.isSignedIn
    }

    suspend fun signOut() {
        Amplify.Auth.signOut()
    }

    suspend fun currentUserEmail(): String? {
        return runCatching { Amplify.Auth.getCurrentUser().username }.getOrNull()
    }

    suspend fun isSignedIn(): Boolean {
        return runCatching { Amplify.Auth.fetchAuthSession().isSignedIn }.getOrDefault(false)
    }
}
