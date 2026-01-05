/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.core.data.service

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.casecode.pos.core.domain.service.GoogleAuthUiClient
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GoogleAuthUiClientImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val googleIdOption: GetGoogleIdOption,
) : GoogleAuthUiClient {

    private val credentialManager = CredentialManager.create(context)

    override suspend fun getIdToken(activity: Context): Result<String> = runCatching {
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

    suspend fun clear() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}
