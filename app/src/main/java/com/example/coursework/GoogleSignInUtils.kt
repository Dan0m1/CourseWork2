package com.example.coursework

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.ui.res.stringResource
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class GoogleSignInUtils {
    companion object {
        fun doGoogleSignIn(
            context: Context,
            scope: CoroutineScope,
            launcher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
            login: (String) -> Unit
        ) {
            val credentialManager: CredentialManager = CredentialManager.create(context)

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(getCredentialOptions(context))
                .build()

            scope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = context
                    )

                    when(result.credential) {
                        is CustomCredential -> {
                            if (result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                                Log.e("GoogleSignIn", "Token: ${googleIdTokenCredential.idToken}")
                                login(googleIdTokenCredential.idToken)
                            }
                        }
                        else -> {
                            Log.e("GoogleSignIn", "Unexpected credential type: ${result.credential::class.java.name}")
                        }
                    }
                } catch (e: NoCredentialException) {
                    Log.e("GoogleSignIn", "No credentials found: ${e.message}")
                    launcher?.launch(getIntent())
                } catch (e: GetCredentialException) {
                    Log.e("GoogleSignIn", "GetCredentialException: ${e.message}")
                    e.printStackTrace()
                } catch (e: Exception) {
                    Log.e("GoogleSignIn", "Unexpected error: ${e.message}")
                    e.printStackTrace()
                }
            }
        }

        fun getCredentialOptions(context: Context): CredentialOption {
            Log.e("GoogleSignIn", "web_client_id: ${context.getString(R.string.web_client_id)}")
            return GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setAutoSelectEnabled(true)
                .setServerClientId(context.getString(R.string.web_client_id))
                .build()
        }

        fun getIntent(): Intent {
            return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
            }
        }
    }
}