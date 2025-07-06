package com.casecode.pos.core.data.service

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.casecode.pos.core.domain.service.GoogleAuthUiClient
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import javax.inject.Inject

class GoogleAuthUiClientImpl @Inject constructor(
    private val googleIdOption: GetGoogleIdOption,
) : GoogleAuthUiClient {


    override suspend fun getIdToken(activity: Context): Result<String> = runCatching {
        val credentialManager = CredentialManager.create(activity)

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(request = request, context = activity)
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)

        googleIdTokenCredential.idToken
    }

    override fun isGooglePlayServicesAvailable(context: Context): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        return apiAvailability.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }
}